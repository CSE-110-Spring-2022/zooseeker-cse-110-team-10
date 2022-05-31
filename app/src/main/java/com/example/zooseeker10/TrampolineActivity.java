package com.example.zooseeker10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Source: codecap.org/create-a-splash-screen-welcom-screen-in-android-studio
 *
 * This class is used to switch to the activity stored on disk
 */
public class TrampolineActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trampoline);

        // Garbage way of passing the activity. Refactor later.
        Globals.State.activity = this;

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent homeIntent = loadIntentFromFile();
                startActivity(homeIntent);
                finish();
            }
        }, Globals.Debug.SPLASH_DELAY);
    }

    /**
     * Called by MainActivity's onCreate to load state data and pull up the last-active Activity
     */
    public Intent loadIntentFromFile() {
        // Sets state in disk and finishes if clean start
        if (StateManager.isCleanStart()) {
            Intent defaultIntent = new Intent(this, SelectionActivity.class);
            defaultIntent.putExtra(Globals.MapKeys.SELECTED_EXHIBIT_IDS, new ArrayList<>());
            return defaultIntent;
        }

        Intent intent;
        // Switches to correct activity and loads relevant files based on last saved state
        Globals.State.ActiveState activeState = (Globals.State.ActiveState) StateManager.getActivityMap(Globals.State.ActiveState.Trampoline).get(Globals.MapKeys.STATE);
        switch (activeState) {
            case Main:
                List<String> selectedExhibitIDs = (ArrayList<String>) StateManager.getActivityMap(Globals.State.ActiveState.Main).get(Globals.MapKeys.SELECTED_EXHIBIT_IDS);
                Log.d("StateManager", "Loaded from MainActivity file: "
                        + Arrays.toString(selectedExhibitIDs.toArray()));

                intent = new Intent(this, SelectionActivity.class);
                intent.putStringArrayListExtra(Globals.MapKeys.SELECTED_EXHIBIT_IDS,
                        (ArrayList<String>) selectedExhibitIDs);
                break;
            case Plan:
                ZooPlan pathZooPlan = (ZooPlan) StateManager.getActivityMap(Globals.State.ActiveState.Plan).get(Globals.MapKeys.ZOOPLAN);

                intent = new Intent(this, PlanActivity.class);
                intent.putExtra(Globals.MapKeys.ZOOPLAN, pathZooPlan);
                break;
            case Directions:
                Map<String, Object> directionsMap = StateManager.getActivityMap(Globals.State.ActiveState.Directions);
                ZooPlan directionsZooPlan = (ZooPlan) directionsMap.get(Globals.MapKeys.ZOOPLAN);
                int walkerIndex = (Integer) directionsMap.get(Globals.MapKeys.WALKER_INDEX);

                intent = new Intent(this, DirectionsActivity.class);
                intent.putExtra(Globals.MapKeys.ZOOPLAN, directionsZooPlan);
                intent.putExtra(Globals.MapKeys.WALKER_INDEX, walkerIndex);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + activeState);
        }

        return intent;
    }
}