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
        ActivityScenario<SelectionActivity> scenario = ActivityScenario.launch(SelectionActivity.class);

        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            Button planButton = activity.findViewById(R.id.plan_btn);
            assertEquals(View.INVISIBLE, planButton.getVisibility());

            activity.selectedExhibits.addExhibit("gorilla");
            assertEquals(1, activity.selectedExhibits.getCount());
            assertEquals(true, activity.selectedExhibits.getExhibitIds().contains("gorilla"));
            assertEquals(View.VISIBLE, planButton.getVisibility());

            activity.selectedExhibits.addExhibit("gorilla");
            assertEquals(1, activity.selectedExhibits.getCount());
        });
    }

    @Test
    public void addSelectExhibitMultipleTest() {
        ActivityScenario<SelectionActivity> scenario = ActivityScenario.launch(SelectionActivity.class);

        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            Button planButton = activity.findViewById(R.id.plan_btn);
            assertEquals(View.INVISIBLE, planButton.getVisibility());

            activity.selectedExhibits.addExhibit("gorilla");
            activity.selectedExhibits.addExhibit("flamingo");
            activity.selectedExhibits.addExhibit("motmot");
            assertEquals(3, activity.selectedExhibits.getCount());
            assertEquals(true, activity.selectedExhibits.getExhibitIds().contains("gorilla"));
            assertEquals(true, activity.selectedExhibits.getExhibitIds().contains("flamingo"));
            assertEquals(true, activity.selectedExhibits.getExhibitIds().contains("motmot"));

            assertEquals(View.VISIBLE, planButton.getVisibility());
        });
    }
}
