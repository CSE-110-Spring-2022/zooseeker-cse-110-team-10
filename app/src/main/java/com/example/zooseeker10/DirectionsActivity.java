package com.example.zooseeker10;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import org.jgrapht.GraphPath;

import java.util.List;
import java.util.Map;

public class DirectionsActivity extends AppCompatActivity {

    public static boolean callReplan;
    private static final boolean listenToGPS = false;
    private Button previousButton;
    private Button nextButton;
    private TextView directionsTitle;

    private ZooPlan plan;
    private ZooPlan.ZooWalker walker;
    private DirectionsListAdapter dLAdapter;
    private Map<String, ZooData.VertexInfo> vertexInfo;
    private PathFinder pf;
    private String lastVertexLocation;
    private UserTracker userTracker;

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
        pf = new PathFinder(ZooData.getZooGraph(this), Globals.ZooData.ENTRANCE_GATE_ID, Globals.ZooData.EXIT_GATE_ID);
        vertexInfo = ZooData.getVertexInfo(this);

        previousButton = findViewById(R.id.directions_previous_button);
        nextButton = findViewById(R.id.directions_next_button);
        RecyclerView recyclerView = findViewById(R.id.directions_list);
        directionsTitle = findViewById(R.id.directions_title);

        var mockButton = findViewById(R.id.mock_btn);
        mockButton.setOnClickListener(this::onMockButtonClicked);

        // Setup for views with text
        dLAdapter = new DirectionsListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dLAdapter);

        // Loads up initial page
        setDirectionsPage(Directions.BACKWARD);
        userTracker = new UserTracker(plan, walker);

        if (listenToGPS) {
            setupLocationListener();
        }

        previousButton.setOnClickListener(view -> setDirectionsPage(Directions.BACKWARD));
        nextButton.setOnClickListener(view -> setDirectionsPage(Directions.FORWARD));
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

        directionsTitle.setText(String.format("Directions to %s",
                vertexInfo.get(walker.getCurrentPath().getEndVertex()).name
        ));
        if (lastVertexLocation == null || lastVertexLocation.equals(walker.getCurrentPath().getStartVertex())) {
            dLAdapter.setDirectionsItems(walker.explainPath(this));
        } else {
            recalculatePath();
            dLAdapter.setDirectionsItems(walker.explainPath(this));
            if (userTracker.needsReplan()) {
                new ReplanPrompt(this).showPrompt();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void setupLocationListener() {
        var provider = LocationManager.GPS_PROVIDER;
        var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        var locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                handleLocationChanged(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        };
        locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
    }

    @VisibleForTesting
    public void handleLocationChanged(@NonNull LatLng currentLocation) {
        Log.d("DirectionsActivity", String.format("Location changed: %s", currentLocation));

        userTracker.setUserLocation(currentLocation);
        lastVertexLocation = userTracker.getClosestVertex().id;
        if (userTracker.isOffTrack()) {
            recalculatePath();
            dLAdapter.setDirectionsItems(walker.explainPath(this));
            if (userTracker.needsReplan()) {
                new ReplanPrompt(this).showPrompt();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void onMockButtonClicked(View view) {
        // TODO: could define this layout in an XML and inflate it, instead of defining in code...
        var inputType = EditorInfo.TYPE_CLASS_NUMBER
            | EditorInfo.TYPE_NUMBER_FLAG_SIGNED
            | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL;

        final EditText latInput = new EditText(this);
        latInput.setInputType(inputType);
        latInput.setHint("Latitude");
        latInput.setText("32.737986");

        final EditText lngInput = new EditText(this);
        lngInput.setInputType(inputType);
        lngInput.setHint("Longitude");
        lngInput.setText("-117.169499");

        final LinearLayout layout = new LinearLayout(this);
        layout.setDividerPadding(8);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(latInput);
        layout.addView(lngInput);

        var builder = new AlertDialog.Builder(this)
            .setTitle("Inject a Mock Location")
            .setView(layout)
            .setPositiveButton("Submit", (dialog, which) -> {
                var lat = Double.parseDouble(latInput.getText().toString());
                var lng = Double.parseDouble(lngInput.getText().toString());
                handleLocationChanged(new LatLng(lat, lng));
            })
            .setNegativeButton("Cancel", (dialog, which) -> {
                dialog.cancel();
            });
        builder.show();
    }

}