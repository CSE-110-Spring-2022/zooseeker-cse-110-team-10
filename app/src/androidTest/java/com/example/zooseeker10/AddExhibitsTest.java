package com.example.zooseeker10;

import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

import java.util.ArrayList;

public class AddExhibitsTest {

    @Test
    public void addAnExhibitTest() {
        String exhibitId = "lions";
        Intent intent
                = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("exhibitId", exhibitId);
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(intent);

        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            Button planButton = activity.findViewById(R.id.plan_btn);
            assertEquals(View.INVISIBLE, planButton.getVisibility());
            activity.selectExhibit("exhibitId");

            assertEquals(true, activity.selectedExhibits.contains(exhibitId));

            assertEquals(false, activity.selectedExhibits.isEmpty());
            assertEquals(1, activity.selectedExhibits.size());
            assertEquals(View.VISIBLE, planButton.getVisibility());
        });
    }

    @Test
    public void addDuplicateExhibitTest() {
        String exhibitId = "gorillas";
        Intent intent
                = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("exhibitId", exhibitId);
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(intent);

        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            activity.selectExhibit("exhibitId");
            assertEquals(false, activity.selectedExhibits.isEmpty());
            assertEquals(1, activity.selectedExhibits.size());
        });

        scenario.moveToState(Lifecycle.State.DESTROYED);
        Intent intent2
                = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent2.putExtra("exhibitId", exhibitId);
        ActivityScenario<MainActivity> scenario2 = ActivityScenario.launch(intent);

        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            activity.selectExhibit("exhibitId");
            assertEquals(false, activity.selectedExhibits.isEmpty());
            assertEquals(1, activity.selectedExhibits.size());
        });


    }
}
