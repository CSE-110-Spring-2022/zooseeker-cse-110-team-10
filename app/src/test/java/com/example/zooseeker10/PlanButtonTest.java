package com.example.zooseeker10;

import static org.junit.Assert.*;

import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PlanButtonTest {

    @Test
    public void planButtonInvisible() {
        ActivityScenario<MainActivity> scenario
                = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            assertEquals(View.INVISIBLE, activity.findViewById(R.id.plan_btn).getVisibility());
        });
    }

    @Test
    public void planButtonVisible() {
        ActivityScenario<MainActivity> scenario
                = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            activity.selectExhibit("lmao animals");
            assertEquals(View.VISIBLE, activity.findViewById(R.id.plan_btn).getVisibility());
        });
    }

}
