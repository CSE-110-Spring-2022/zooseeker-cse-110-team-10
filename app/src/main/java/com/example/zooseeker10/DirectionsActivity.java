package com.example.zooseeker10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Map;

public class DirectionsActivity extends AppCompatActivity {
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    public static boolean isBriefDirections;

    public static boolean callReplan;
    Button previousButton;
    Button nextButton;
    ImageButton settingsButton;
    TextView directionsTitle;

    ZooPlan plan;
    ZooPlan.ZooWalker walker;
    DirectionsListAdapter dLAdapter;
    Map<String, ZooData.VertexInfo> vertexInfo;

    enum Directions {
        FORWARD,
        BACKWARD
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        Intent intent = getIntent();
        plan = (ZooPlan)intent.getSerializableExtra("paths");
        walker = plan.startWalker();
        vertexInfo = ZooData.getVertexInfo(this);

        previousButton = findViewById(R.id.directions_previous_button);
        nextButton = findViewById(R.id.directions_next_button);
        settingsButton = findViewById(R.id.settings_button);
        RecyclerView recyclerView = findViewById(R.id.directions_list);
        directionsTitle = findViewById(R.id.directions_title);

        // Setup for views with text
        dLAdapter = new DirectionsListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dLAdapter);

        // Loads up initial page
        setDirectionsPage(Directions.BACKWARD);

        OffTrackDetector locationDetector = new OffTrackDetector(this, plan, walker);

        /*Listen for Location Updates*/
        {
            var provider = LocationManager.GPS_PROVIDER;
            var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            var locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Log.d("DirectionsActivity", String.format("Location changed: %s", location));

                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

//                    if (locationDetector.isOffTrack(currentLocation)) {
//                        Log.d("DirectionsActivity", String.format("BRUH YOU OFF TRACK!! GET OUTTA HERE"));
//                    }
                }
            };
            locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
        }

        previousButton.setOnClickListener(
                view -> { setDirectionsPage(Directions.BACKWARD); updateDirectionsType();}
        );

        nextButton.setOnClickListener(
                view -> { setDirectionsPage(Directions.FORWARD); updateDirectionsType();}
        );

    }

    /**
     * Citation:
     * https://stackoverflow.com/questions/920306/sending-data-back-to-the-main-activity-in-android
     * Sending the data back to main activity in android
     * May 27th, 2022
     * Used mainly as a source of info and template for opening a new activity for result
     * D.J
     */
    public void onSettingsButtonClicked(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        Log.d("Settings Activity: ", "Started");
        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Citation:
     * https://stackoverflow.com/questions/920306/sending-data-back-to-the-main-activity-in-android
     * Sending the data back to main activity in android
     * May 27th, 2022
     * Used mainly as a source of info and template for retrieving data from a finished activity
     * D.J
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                isBriefDirections = data.getBooleanExtra("key", false);
                updateDirectionsType();
            }
        }
    }

    public void updateDirectionsType(){
        List<DirectionsItem> displayedDirections = walker.explainPath(this, isBriefDirections);
        dLAdapter.setDirectionsItems(displayedDirections);
        directionsTitle.setText(String.format("Directions from %s to %s",
                displayedDirections.get(0).from,
                displayedDirections.get(displayedDirections.size() - 1).to
        ));
    }

    public void setDirectionsPage(Directions d) {
        if (d == Directions.FORWARD){
            walker.traverseForward();
        }
        else {
            walker.traverseBackward();
        }

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

        List<DirectionsItem> displayedDirections = walker.explainPath(this,false);
        dLAdapter.setDirectionsItems(displayedDirections);
        directionsTitle.setText(String.format("Directions from %s to %s",
                displayedDirections.get(0).from,
                displayedDirections.get(displayedDirections.size() - 1).to
        ));
    }
}