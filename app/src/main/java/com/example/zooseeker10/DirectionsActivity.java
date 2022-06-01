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

import java.util.ArrayList;
import org.jgrapht.GraphPath;

import java.util.List;
import java.util.Map;

public class DirectionsActivity extends AppCompatActivity {
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    @VisibleForTesting
    public boolean isBriefDirections;

    private static final boolean listenToGPS = false;
    private Button previousButton;
    private Button nextButton;
    private TextView directionsTitle;
    private Button skipButton;
    private Button finishButton;

    @VisibleForTesting
    public ZooPlan plan;
    private ZooPlan.ZooWalker walker;
    private DirectionsListAdapter dLAdapter;
    private Map<String, ZooData.VertexInfo> vertexInfo;
    private PathFinder pf;
    private String lastVertexLocation;
    private UserTracker userTracker;
    private ReplanPrompt replanPrompt;
    private ReplanMessageDisplay replanMessageDisplay;

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
        skipButton = findViewById(R.id.skip_button);
        finishButton = findViewById(R.id.directions_finish_button);
        RecyclerView recyclerView = findViewById(R.id.directions_list);
        directionsTitle = findViewById(R.id.directions_title);

        var mockButton = findViewById(R.id.mock_btn);
        mockButton.setOnClickListener(this::onMockButtonClicked);

        // Setup for views with text
        dLAdapter = new DirectionsListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dLAdapter);

        // Loads up initial page
        reloadDirectionsPage();
        userTracker = new UserTracker(plan, walker);

        /* Listen for Location Updates */
        if (listenToGPS) {
            setupLocationListener();
        }

        replanPrompt = new ReplanPrompt(this);
        replanMessageDisplay = new ReplanMessageDisplay(this);

        previousButton.setOnClickListener(view -> {
            replanPrompt.enablePrompt();
            walker.traverseBackward();
            reloadDirectionsPage();
        });

        nextButton.setOnClickListener(view -> {
            replanPrompt.enablePrompt();
            walker.traverseForward();
            reloadDirectionsPage();
        });

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
        StateManager.getSingleton(this).storeDirectionsState(plan, walker.getCurrentExhibitIndex());
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
                setIsBriefDirections(data.getBooleanExtra("key", false));
            }
        }
    }

    @VisibleForTesting
    public void setIsBriefDirections(boolean isBriefDirections) {
        this.isBriefDirections = isBriefDirections;
        dLAdapter.setDirectionsItems(walker.explainPath(this, isBriefDirections));
    }

    /**
     * Calculate directions from user's current location to current destination.
     * Requires that a location has been set.
     */
    public void recalculatePath() {
        GraphPath<String, IdentifiedWeightedEdge> newPath = pf.getShortest(lastVertexLocation, walker.getCurrentPath().getEndVertex());
        plan.replan(walker, newPath);
    }

    /**
     * Load the directions for a potentially new page,
     *     including title and button visibility.
     * Does not require that a location has ever been set.
     * Directions will start from current location if possible
     *    (even if user is on-track).
     */
    public void reloadDirectionsPage() {
        // set visibility of previous/next buttons by hasPrevious/hasNext
        previousButton.setVisibility(walker.hasPrevious() ? View.VISIBLE : View.INVISIBLE);
        nextButton.setVisibility(walker.hasNext() ? View.VISIBLE : View.INVISIBLE);
        skipButton.setVisibility(walker.hasNext() ? View.VISIBLE : View.INVISIBLE);
        finishButton.setVisibility((!walker.hasNext()) ? View.VISIBLE : View.INVISIBLE);

        directionsTitle.setText(String.format("Directions to %s",
                vertexInfo.get(walker.getCurrentPath().getEndVertex()).name
        ));
        if (lastVertexLocation == null || lastVertexLocation.equals(walker.getCurrentPath().getStartVertex())) {
            dLAdapter.setDirectionsItems(walker.explainPath(this, isBriefDirections));
        } else {
            if (userTracker.needsReplan()) {
                replanPrompt.showPrompt();
            } else {
                recalculatePath();
                dLAdapter.setDirectionsItems(walker.explainPath(this, isBriefDirections));
            }
        }
    }

    /**
     * Reload the directions on this page.
     * Requires that a user location has been set.
     * Directions will be changed only if user is off-track.
     */
    public void reloadDirections() {
        if (userTracker.isOffTrack() && userTracker.needsReplan()) {
                Log.d("DirectionsActivity", "replan asked");
                replanPrompt.showPrompt();
        } else if (userTracker.isOffTrack()) {
            recalculatePath();
            dLAdapter.setDirectionsItems(walker.explainPath(this, isBriefDirections));
        }
    }

    @VisibleForTesting
    public void onReplanRequested() {
        replanPrompt.enablePrompt();
        Log.d("DirectionsActivity", "replan accepted");
        List<String> replannableExhibits = plan.getReplannable(walker);
        ZooPlan newPlan = pf.findPath(replannableExhibits, lastVertexLocation);
        plan.replan(walker, newPlan);
        reloadDirectionsPage();
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

        Location location = locationManager.getLastKnownLocation(provider);
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        handleLocationChanged(currentLatLng);
    }

    @VisibleForTesting
    public void handleLocationChanged(@NonNull LatLng currentLocation) {
        Log.d("DirectionsActivity", String.format("Location changed: %s", currentLocation));

        userTracker.setUserLocation(currentLocation);
        lastVertexLocation = userTracker.getClosestVertex().id;
        reloadDirections();
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

    public void onSkipButtonClicked(View view) {
        String skippedExhibitID = walker.getNextExhibitID();
        Log.d("DirectionsActivity", String.format("Skipped exhibit: %s", skippedExhibitID));
        plan.remove(walker.getCurrentExhibitIndex());
        List<String> replannableExhibits = plan.getReplannable(walker);
        lastVertexLocation = userTracker.getClosestVertex().id;
        ZooPlan newPlan = pf.findPath(replannableExhibits, lastVertexLocation);
        plan.replan(walker, newPlan);
        replanMessageDisplay.showPrompt();
        reloadDirectionsPage();
    }
}