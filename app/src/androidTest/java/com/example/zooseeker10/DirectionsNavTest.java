package com.example.zooseeker10;

import static org.junit.Assert.*;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.locks.Condition;

@RunWith(AndroidJUnit4.class)
public class DirectionsNavTest {

    @Test
    public void testDirectionsNav() {
        String paths = "[" +
            "[\"entrance_exit_gate\",\"edge-0\",\"entrance_plaza\",\"edge-4\",\"arctic_foxes\"]," +
            "[\"arctic_foxes\",\"edge-4\",\"entrance_plaza\",\"edge-5\",\"gators\",\"edge-6\",\"lions\"]" +
        "]";
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DirectionsActivity.class);
        intent.putExtra("paths", paths);
        ActivityScenario<DirectionsActivity> scenario
                = ActivityScenario.launch(intent);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            TextView t = activity.findViewById(R.id.directions_title);
            RecyclerView rv = activity.findViewById(R.id.directions_list);
            DirectionsListAdapter a = (DirectionsListAdapter) rv.getAdapter();

            Button nb = activity.findViewById(R.id.directions_next_button);
            Button pb = activity.findViewById(R.id.directions_previous_button);

            assertEquals("Directions from Entrance and Exit Gate\nto Arctic Foxes", t.getText());
            assertEquals(2, a.getItemCount());
            assertEquals("Entrance and Exit Gate", a.getItemAt(0).from);
            assertEquals("Entrance Way", a.getItemAt(0).street);
            assertEquals("Entrance Plaza", a.getItemAt(0).to);
            assertEquals(10.0, a.getItemAt(0).dist, 1e-7);
            assertEquals("Entrance Plaza", a.getItemAt(1).from);
            assertEquals("Arctic Avenue", a.getItemAt(1).street);
            assertEquals("Arctic Foxes", a.getItemAt(1).to);
            assertEquals(300.0, a.getItemAt(1).dist, 1e-7);
            assertEquals(View.INVISIBLE, pb.getVisibility());
            assertEquals(View.VISIBLE, nb.getVisibility());

            nb.performClick();

            assertEquals("Directions from Arctic Foxes\nto Lions", t.getText());
            assertEquals(3, rv.getAdapter().getItemCount());
            assertEquals("Arctic Foxes", a.getItemAt(0).from);
            assertEquals("Arctic Avenue", a.getItemAt(0).street);
            assertEquals("Entrance Plaza", a.getItemAt(0).to);
            assertEquals(300.0, a.getItemAt(0).dist, 1e-7);
            assertEquals("Entrance Plaza", a.getItemAt(1).from);
            assertEquals("Reptile Road", a.getItemAt(1).street);
            assertEquals("Alligators", a.getItemAt(1).to);
            assertEquals(100.0, a.getItemAt(1).dist, 1e-7);
            assertEquals("Alligators", a.getItemAt(2).from);
            assertEquals("Sharp Teeth Shortcut", a.getItemAt(2).street);
            assertEquals("Lions", a.getItemAt(2).to);
            assertEquals(200.0, a.getItemAt(2).dist, 1e-7);
            assertEquals(View.VISIBLE, pb.getVisibility());
            assertEquals(View.INVISIBLE, nb.getVisibility());
        });
    }

}
