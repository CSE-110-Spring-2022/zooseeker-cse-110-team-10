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

import java.util.ArrayList;
import org.jgrapht.GraphPath;

import java.util.List;
import java.util.Map;

public class DirectionsActivity extends AppCompatActivity {

    Button previousButton;
    Button nextButton;
    Button finishButton;
    TextView directionsTitle;

    ZooPlan plan;
    ZooPlan.ZooWalker walker;
    DirectionsListAdapter dLAdapter;
    Map<String, ZooData.VertexInfo> vertexInfo;

    PathFinder pf;
    String lastVertexLocation;
    ReplanPrompt replanPrompt;
    UserTracker userTracker;

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

        pf = new PathFinder(ZooData.getZooGraph(this), Globals.ZooData.ENTRANCE_GATE_ID, Globals.ZooData.EXIT_GATE_ID);

        vertexInfo = ZooData.getVertexInfo(this);

        previousButton = findViewById(R.id.directions_previous_button);
        nextButton = findViewById(R.id.directions_next_button);
        finishButton = findViewById(R.id.directions_finish_button);
        RecyclerView recyclerView = findViewById(R.id.directions_list);
        directionsTitle = findViewById(R.id.directions_title);

        // Setup for views with text
        dLAdapter = new DirectionsListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dLAdapter);

        // Loads up initial page
        refreshDirections();
        userTracker = new UserTracker(plan, walker);

        /* Listen for Location Updates */
        {
            var provider = LocationManager.GPS_PROVIDER;
            var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            var locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
//                    Log.d("DirectionsActivity", String.format("Location changed: %s", location));
//
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    userTracker.setUserLocation(currentLocation);
                    lastVertexLocation = userTracker.getClosestVertex().id;
                    if (userTracker.isOffTrack()) {
                        recalculatePath();
                        if (userTracker.needsReplan()) {
                            Log.d("DirectionsActivity", "replan asked");
                            replanPrompt.showPrompt();
                        }
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

        replanPrompt = new ReplanPrompt(this);

        previousButton.setOnClickListener(
                view -> {
                    replanPrompt.enablePrompt();
                    walker.traverseBackward();
                    refreshDirections();
                }
        );

        nextButton.setOnClickListener(
                view -> {
                    replanPrompt.enablePrompt();
                    walker.traverseForward();
                    refreshDirections();
                }
        );

        finishButton.setOnClickListener(
                view -> {
                    Intent finishIntent = new Intent(this, SelectionActivity.class);
                    finishIntent.putExtra(Globals.MapKeys.SELECTED_EXHIBIT_IDS, new ArrayList<String>());
                    startActivity(finishIntent);
                }
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        StateManager.storeDirectionsState(plan, walker.getCurrentExhibitIndex());
    }

    @Override
    public void onBackPressed() {
        if (Globals.Debug.USE_BACK) {
            Intent intent = new Intent(this, PlanActivity.class);
            intent.putExtra(Globals.MapKeys.ZOOPLAN, plan);
            startActivity(intent);
        }
        else {
            super.onBackPressed();
        }
    }

    public void recalculatePath() {
        GraphPath<String, IdentifiedWeightedEdge> newPath = pf.getShortest(lastVertexLocation, walker.getCurrentPath().getEndVertex());
        plan.replan(walker, newPath);
    }

    public void refreshDirections() {
        previousButton.setVisibility((walker.hasPrevious()) ? View.VISIBLE : View.INVISIBLE);
        nextButton.setVisibility((walker.hasNext()) ? View.VISIBLE : View.INVISIBLE);
        finishButton.setVisibility((!walker.hasNext()) ? View.VISIBLE : View.INVISIBLE);

        // update visible directions
        List<DirectionsItem> displayedDirections = walker.explainPath(this);
        dLAdapter.setDirectionsItems(displayedDirections);
        directionsTitle.setText(String.format("Directions from %s to %s",
                displayedDirections.get(0).from,
                displayedDirections.get(displayedDirections.size() - 1).to
        ));
    }

    public void onReplanRequested() {
        replanPrompt.enablePrompt();
        List<String> replannableExhibits = plan.getReplannable(walker);
        ZooPlan newPlan = pf.findPath(replannableExhibits, lastVertexLocation);
        plan.replan(walker, newPlan);
        refreshDirections();
    }
}