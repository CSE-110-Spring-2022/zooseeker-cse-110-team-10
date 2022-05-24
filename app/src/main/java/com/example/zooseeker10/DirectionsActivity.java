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
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Map;

public class DirectionsActivity extends AppCompatActivity {

    Button previousButton;
    Button nextButton;
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
        walker = plan.new ZooWalker(0);
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

                    if (locationDetector.isOffTrack(currentLocation)) {
                        Log.d("DirectionsActivity", String.format("BRUH YOU OFF TRACK!! GET OUTTA HERE"));
                    }
                }
            };
            locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
        }

        previousButton.setOnClickListener(
                view -> { setDirectionsPage(Directions.BACKWARD); }
        );

        nextButton.setOnClickListener(
                view -> { setDirectionsPage(Directions.FORWARD); }
        );

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

        List<DirectionsItem> displayedDirections = walker.explainPath(this);
        dLAdapter.setDirectionsItems(displayedDirections);
        directionsTitle.setText(String.format("Directions from %s to %s",
                displayedDirections.get(0).from,
                displayedDirections.get(displayedDirections.size() - 1).to
        ));
    }
}