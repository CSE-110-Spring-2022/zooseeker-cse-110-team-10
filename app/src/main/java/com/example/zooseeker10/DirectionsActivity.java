package com.example.zooseeker10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

public class DirectionsActivity extends AppCompatActivity {

    public static boolean callReplan;
    Button previousButton;
    Button nextButton;
    TextView directionsTitle;

    ZooPlan plan;
    ZooPlan.ZooWalker walker;
    DirectionsListAdapter dLAdapter;
    Map<String, ZooData.VertexInfo> vertexInfo;

    File stateFile;

    /**
     * Source: https://www.mysamplecode.com/2012/06/android-internal-external-storage.html
     *
     * @param savedInstanceState
     */
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir(Globals.Directions.STATE_FILEPATH, Context.MODE_PRIVATE);
        stateFile = new File(directory, Globals.Directions.STATE_FILENAME);

        if (!stateFile.exists()) {
            Intent intent = getIntent();
            plan = (ZooPlan)intent.getSerializableExtra("paths");
            walker = plan.startWalker();

            storeState();
        }
        else {
            loadState();
        }

        vertexInfo = ZooData.getVertexInfo(this);

        previousButton = findViewById(R.id.directions_previous_button);
        nextButton = findViewById(R.id.directions_next_button);
        RecyclerView recyclerView = findViewById(R.id.directions_list);
        directionsTitle = findViewById(R.id.directions_title);

        // Setup for views with text
        dLAdapter = new DirectionsListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dLAdapter);

        // Loads up initial page
        refreshDirections();

        OffTrackDetector locationDetector = new OffTrackDetector(this, plan, walker);

        /*Listen for Location Updates*/
        {
            var provider = LocationManager.GPS_PROVIDER;
            var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            var locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
//                    Log.d("DirectionsActivity", String.format("Location changed: %s", location));
//
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

//                    if (locationDetector.isOffTrack(currentLocation)) {
//                        Log.d("DirectionsActivity", String.format("BRUH YOU OFF TRACK!! GET OUTTA HERE"));
//                    }
                }
            };
            locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
        }

        previousButton.setOnClickListener(
                view -> {
                    walker.traverseBackward();
                    refreshDirections();
                }
        );

        nextButton.setOnClickListener(
                view -> {
                    walker.traverseForward();
                    refreshDirections();
                }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadState();
    }

    @Override
    protected void onStop() {
        super.onStop();

        storeState();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        storeState();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        loadState();
    }

    private void storeState() {
        try {
            // Saving of object in a file
            FileOutputStream file = new FileOutputStream
                    (stateFile);
            ObjectOutputStream out = new ObjectOutputStream
                    (file);

            // Method for serialization of object
            out.writeObject(plan);
            out.writeObject(walker);
            Log.d("Directions", "Directions state has been stored");

            out.close();
            file.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadState() {
        try {

            // Reading the object from a file
            FileInputStream file = new FileInputStream
                    (stateFile);
            ObjectInputStream in = new ObjectInputStream
                    (file);

            // Method for deserialization of object
            plan = (ZooPlan) in.readObject();
            walker = (ZooPlan.ZooWalker) in.readObject();
            Log.d("Directions", "Directions state has been loaded");

            in.close();
            file.close();
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void refreshDirections() {
        if (!walker.hasPrevious()) {
            previousButton.setVisibility(View.INVISIBLE);
        } else {
            previousButton.setVisibility(View.VISIBLE);
        }
        if (!walker.hasNext()) {
            nextButton.setVisibility(View.INVISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
        }

        List<DirectionsItem> displayedDirections = walker.explainPath(this);
        dLAdapter.setDirectionsItems(displayedDirections);
        directionsTitle.setText(String.format("Directions from %s to %s",
                displayedDirections.get(0).from,
                displayedDirections.get(displayedDirections.size() - 1).to
        ));
    }
}