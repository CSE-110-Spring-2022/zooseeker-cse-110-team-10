package com.example.zooseeker10;

import static org.junit.Assert.assertEquals;

import android.view.View;
import android.widget.Button;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SelectExhibitTest {

    @Test
    public void addSelectExhibitDuplicateTest() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            Button planButton = activity.findViewById(R.id.plan_btn);
            assertEquals(View.INVISIBLE, planButton.getVisibility());

            activity.selectExhibit("gorillas");
            assertEquals(false, activity.selectedExhibits.selectedExhibitIds.isEmpty());
            assertEquals(1, activity.selectedExhibits.selectedExhibitIds.size());
            assertEquals(true, activity.selectedExhibits.selectedExhibitIds.contains("gorillas"));
            assertEquals(View.VISIBLE, planButton.getVisibility());

            activity.selectExhibit("gorillas");
            assertEquals(1, activity.selectedExhibits.selectedExhibitIds.size());
        });
    }

    @Test
    public void addSelectExhibitMultipleTest() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            Button planButton = activity.findViewById(R.id.plan_btn);
            assertEquals(View.INVISIBLE, planButton.getVisibility());

            activity.selectExhibit("gorillas");
            activity.selectExhibit("lions");
            activity.selectExhibit("elephants");
            assertEquals(false, activity.selectedExhibits.selectedExhibitIds.isEmpty());
            assertEquals(3, activity.selectedExhibits.selectedExhibitIds.size());
            assertEquals(true, activity.selectedExhibits.selectedExhibitIds.contains("gorillas"));
            assertEquals(true, activity.selectedExhibits.selectedExhibitIds.contains("lions"));
            assertEquals(true, activity.selectedExhibits.selectedExhibitIds.contains("elephants"));

            assertEquals(View.VISIBLE, planButton.getVisibility());
        });
    }
}
