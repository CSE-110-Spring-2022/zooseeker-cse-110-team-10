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

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

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
    PathFinder pf;
    String lastVertexLocation;

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
        plan = (ZooPlan) intent.getSerializableExtra("paths");
        walker = plan.startWalker();
        pf = new PathFinder(ZooData.getZooGraph(this), ZooData.ENTRANCE_GATE_ID, ZooData.EXIT_GATE_ID);
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
        UserTracker userTracker = new UserTracker(plan, walker);

        /* Listen for Location Updates */
        {
            var provider = LocationManager.GPS_PROVIDER;
            var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            var locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Log.d("DirectionsActivity", String.format("Location changed: %s", location));

                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    userTracker.setUserLocation(currentLocation);
                    lastVertexLocation = userTracker.getClosestVertex().id;
                    if (userTracker.isOffTrack()) {
                        recalculatePath();
                    }
                }
            };
            locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);

            Location location = locationManager.getLastKnownLocation(provider);
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            userTracker.setUserLocation(currentLatLng);
            lastVertexLocation = userTracker.getClosestVertex().id;
            if (userTracker.isOffTrack()) {
                recalculatePath();
            }
        }

        previousButton.setOnClickListener(
                view -> {
                    setDirectionsPage(Directions.BACKWARD);
                }
        );

        nextButton.setOnClickListener(
                view -> {
                    setDirectionsPage(Directions.FORWARD);
                }
        );

    }

    public void recalculatePath() {
        GraphPath<String, IdentifiedWeightedEdge> newPath = pf.getShortest(lastVertexLocation, walker.getCurrentPath().getEndVertex());
        plan.replan(walker, newPath);
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