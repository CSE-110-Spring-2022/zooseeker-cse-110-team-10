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

import org.jgrapht.Graph;
import org.jgrapht.graph.GraphWalk;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;

@RunWith(AndroidJUnit4.class)
public class DirectionsNavTest {

    public static final double DOUBLE_EPSILON = 1e-7;
    private static Graph<String, IdentifiedWeightedEdge> graph;

    /**
     * Imports the graph topology from the example file
     */
    @Before
    public void setup() {
        graph = ZooData.loadZooGraphJSON(ApplicationProvider.getApplicationContext(), ZooData.ZOO_GRAPH_PATH);
    }

    @Test
    public void testDirectionsNav() {
        String paths = "[" +
            "[\"entrance_exit_gate\",\"edge-0\",\"entrance_plaza\",\"edge-4\",\"arctic_foxes\"]," +
            "[\"arctic_foxes\",\"edge-4\",\"entrance_plaza\",\"edge-5\",\"gators\",\"edge-6\",\"lions\"]" +
        "]";
        ZooPlan plan = new ZooPlan(Arrays.asList(
                new GraphWalk<>(graph, Arrays.asList("entrance_exit_gate", "entrance_plaza", "arctic_foxes"), 310.0),
                new GraphWalk<>(graph, Arrays.asList("arctic_foxes", "entrance_plaza", "gators", "lions"), 600.0)
        ));
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DirectionsActivity.class);
        intent.putExtra("paths", plan);
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

            assertEquals("Directions from Entrance and Exit Gate to Arctic Foxes", t.getText());
            assertEquals(2, a.getItemCount());
            assertEquals("Entrance and Exit Gate", a.getItemAt(0).from);
            assertEquals("Entrance Way", a.getItemAt(0).street);
            assertEquals("Entrance Plaza", a.getItemAt(0).to);
            assertEquals(10.0, a.getItemAt(0).dist, DOUBLE_EPSILON);
            assertEquals("Entrance Plaza", a.getItemAt(1).from);
            assertEquals("Arctic Avenue", a.getItemAt(1).street);
            assertEquals("Arctic Foxes", a.getItemAt(1).to);
            assertEquals(300.0, a.getItemAt(1).dist, DOUBLE_EPSILON);
            assertEquals(View.INVISIBLE, pb.getVisibility());
            assertEquals(View.VISIBLE, nb.getVisibility());

            nb.performClick();

            assertEquals("Directions from Arctic Foxes to Lions", t.getText());
            assertEquals(3, rv.getAdapter().getItemCount());
            assertEquals("Arctic Foxes", a.getItemAt(0).from);
            assertEquals("Arctic Avenue", a.getItemAt(0).street);
            assertEquals("Entrance Plaza", a.getItemAt(0).to);
            assertEquals(300.0, a.getItemAt(0).dist, DOUBLE_EPSILON);
            assertEquals("Entrance Plaza", a.getItemAt(1).from);
            assertEquals("Reptile Road", a.getItemAt(1).street);
            assertEquals("Alligators", a.getItemAt(1).to);
            assertEquals(100.0, a.getItemAt(1).dist, DOUBLE_EPSILON);
            assertEquals("Alligators", a.getItemAt(2).from);
            assertEquals("Sharp Teeth Shortcut", a.getItemAt(2).street);
            assertEquals("Lions", a.getItemAt(2).to);
            assertEquals(200.0, a.getItemAt(2).dist, DOUBLE_EPSILON);
            assertEquals(View.VISIBLE, pb.getVisibility());
            assertEquals(View.INVISIBLE, nb.getVisibility());
        });
    }

}
