package com.example.zooseeker10;

import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.base.MainThread;

import org.junit.Test;

import java.util.ArrayList;

public class AddExhibitsTest {

    @Test
    public void addSelectExhibitTest() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            Button planButton = activity.findViewById(R.id.plan_btn);
            assertEquals(View.INVISIBLE, planButton.getVisibility());

            activity.selectExhibit("gorillas");
            assertEquals(false, activity.selectedExhibits.isEmpty());
            assertEquals(1, activity.selectedExhibits.size());
            assertEquals(true, activity.selectedExhibits.contains("gorillas"));
            assertEquals(View.VISIBLE, planButton.getVisibility());

            activity.selectExhibit("gorillas");
            assertEquals(1, activity.selectedExhibits.size());


        });
    }
}
