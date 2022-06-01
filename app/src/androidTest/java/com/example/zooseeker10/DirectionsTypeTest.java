package com.example.zooseeker10;

import static org.junit.Assert.assertEquals;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DirectionsTypeTest {
    public static final double DOUBLE_EPSILON = 1e-7;
    private static Graph<String, IdentifiedWeightedEdge> graph;
    private static PathFinder pathfinder;
    private static Context context;

    /**
     * Imports the graph topology from the example file
     */
    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        graph = ZooData.getZooGraph(context);
        pathfinder = new PathFinder(graph, Globals.ZooDataTest.ENTRANCE_GATE_ID, Globals.ZooDataTest.EXIT_GATE_ID);
    }

    @Test
    public void testBriefDirections() {
        ZooPlan plan = new ZooPlan(Arrays.asList(
            new GraphWalk<>(graph, Arrays.asList("entrance_exit_gate", "intxn_front_treetops", "intxn_treetops_fern_trail", "intxn_treetops_orangutan_trail"), 8700.0),
            new GraphWalk<>(graph, Arrays.asList("intxn_treetops_orangutan_trail", "intxn_treetops_fern_trail", "intxn_front_treetops", "entrance_exit_gate"), 8700.0)
        ));
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DirectionsActivity.class);
        intent.putExtra(Globals.MapKeys.ZOOPLAN, plan);
        ActivityScenario<DirectionsActivity> scenario
                = ActivityScenario.launch(intent);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.directions_list);
            DirectionsListAdapter a = (DirectionsListAdapter) recyclerView.getAdapter();

            activity.setIsBriefDirections(true);
            assertEquals(2, a.getItemCount());
            assertEquals("Entrance and Exit Gate", a.getItemAt(0).from);
            assertEquals("Gate Path", a.getItemAt(0).street);
            assertEquals("Front Street / Treetops Way", a.getItemAt(0).to);
            assertEquals(1100.0, a.getItemAt(0).dist, DOUBLE_EPSILON);
            assertEquals("Front Street / Treetops Way", a.getItemAt(1).from);
            assertEquals("Treetops Way", a.getItemAt(1).street);
            assertEquals("Treetops Way / Orangutan Trail", a.getItemAt(1).to);
            assertEquals(2500.0, a.getItemAt(1).dist, DOUBLE_EPSILON);

            activity.findViewById(R.id.directions_next_button).performClick();

            activity.setIsBriefDirections(false);
            assertEquals(3, a.getItemCount());
            assertEquals("Treetops Way / Orangutan Trail", a.getItemAt(0).from);
            assertEquals("Treetops Way", a.getItemAt(0).street);
            assertEquals("Treetops Way / Fern Canyon Trail", a.getItemAt(0).to);
            assertEquals(1400.0, a.getItemAt(0).dist, DOUBLE_EPSILON);
            assertEquals("Treetops Way / Fern Canyon Trail", a.getItemAt(1).from);
            assertEquals("Treetops Way", a.getItemAt(1).street);
            assertEquals("Front Street / Treetops Way", a.getItemAt(1).to);
            assertEquals(1100.0, a.getItemAt(1).dist, DOUBLE_EPSILON);
            assertEquals("Front Street / Treetops Way", a.getItemAt(2).from);
            assertEquals("Gate Path", a.getItemAt(2).street);
            assertEquals("Entrance and Exit Gate", a.getItemAt(2).to);
            assertEquals(1100.0, a.getItemAt(2).dist, DOUBLE_EPSILON);
        });
    }
}
