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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

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

        Intent intent = getIntent();
        plan = (ZooPlan) intent.getSerializableExtra(Globals.MapKeys.ZOOPLAN);
        int walkerIndex = intent.getIntExtra(Globals.MapKeys.WALKER_INDEX, 0);
        walker = plan.new ZooWalker(walkerIndex);

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
    protected void onStop() {
        super.onStop();

        StateManager.storeDirectionsState(plan, walker.getCurrentExhibitIndex());
    }

    public void refreshDirections() {
        previousButton.setVisibility((walker.hasPrevious()) ? View.VISIBLE : View.INVISIBLE);
        nextButton.setVisibility((walker.hasNext()) ? View.VISIBLE : View.INVISIBLE);

        List<DirectionsItem> displayedDirections = walker.explainPath(this);
        dLAdapter.setDirectionsItems(displayedDirections);
        directionsTitle.setText(String.format("Directions from %s to %s",
                displayedDirections.get(0).from,
                displayedDirections.get(displayedDirections.size() - 1).to
        ));
    }
}