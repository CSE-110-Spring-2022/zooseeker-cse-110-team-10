package com.example.zooseeker10;

import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class SelectExhibitTest {

    @Test
    public void addSelectExhibitDuplicateTest() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SelectionActivity.class);
        intent.putStringArrayListExtra(Globals.MapKeys.SELECTED_EXHIBIT_IDS, new ArrayList<>());
        ActivityScenario<SelectionActivity> scenario
                = ActivityScenario.launch(intent);

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
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SelectionActivity.class);
        intent.putStringArrayListExtra(Globals.MapKeys.SELECTED_EXHIBIT_IDS, new ArrayList<>());
        ActivityScenario<SelectionActivity> scenario
                = ActivityScenario.launch(intent);

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
