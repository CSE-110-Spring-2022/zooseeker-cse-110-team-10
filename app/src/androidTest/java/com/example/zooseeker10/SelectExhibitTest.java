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

            activity.selectedExhibits.addExhibit("gorillas");
            assertEquals(1, activity.selectedExhibits.getCount());
            assertEquals(true, activity.selectedExhibits.getExhibitIds().contains("gorillas"));
            assertEquals(View.VISIBLE, planButton.getVisibility());

            activity.selectedExhibits.addExhibit("gorillas");
            assertEquals(1, activity.selectedExhibits.getCount());
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

            activity.selectedExhibits.addExhibit("gorillas");
            activity.selectedExhibits.addExhibit("lions");
            activity.selectedExhibits.addExhibit("elephant_odyssey");
            assertEquals(3, activity.selectedExhibits.getCount());
            assertEquals(true, activity.selectedExhibits.getExhibitIds().contains("gorillas"));
            assertEquals(true, activity.selectedExhibits.getExhibitIds().contains("lions"));
            assertEquals(true, activity.selectedExhibits.getExhibitIds().contains("elephant_odyssey"));

            assertEquals(View.VISIBLE, planButton.getVisibility());
        });
    }
}
