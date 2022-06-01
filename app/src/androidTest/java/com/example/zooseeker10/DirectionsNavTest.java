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

import java.util.Arrays;

@RunWith(AndroidJUnit4.class)
public class DirectionsNavTest {

    public static final double DOUBLE_EPSILON = 1e-7;
    private static Graph<String, IdentifiedWeightedEdge> graph;

    /**
     * Imports the graph topology from the example file
     */
    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();

        graph = ZooData.getZooGraph(context);
    }


    @Test
    public void testDirectionsNav() {
        ZooPlan plan = new ZooPlan(Arrays.asList(
                new GraphWalk<>(graph, Arrays.asList("entrance_exit_gate", "intxn_front_treetops", "intxn_front_monkey", "flamingo"), 5300.0),
                new GraphWalk<>(graph, Arrays.asList("flamingo", "capuchin"), 3100.0)
        ));
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DirectionsActivity.class);
        intent.putExtra(Globals.MapKeys.ZOOPLAN, plan);
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

            assertEquals("Directions from Entrance and Exit Gate to Flamingos", t.getText());
            assertEquals(3, a.getItemCount());
            assertEquals("Entrance and Exit Gate", a.getItemAt(0).from);
            assertEquals("Gate Path", a.getItemAt(0).street);
            assertEquals("Front Street / Treetops Way", a.getItemAt(0).to);
            assertEquals(1100.0, a.getItemAt(0).dist, DOUBLE_EPSILON);
            assertEquals("Front Street / Treetops Way", a.getItemAt(1).from);
            assertEquals("Front Street", a.getItemAt(1).street);
            assertEquals("Front Street / Monkey Trail", a.getItemAt(1).to);
            assertEquals(2700.0, a.getItemAt(1).dist, DOUBLE_EPSILON);
            assertEquals("Front Street / Monkey Trail", a.getItemAt(2).from);
            assertEquals("Monkey Trail", a.getItemAt(2).street);
            assertEquals("Flamingos", a.getItemAt(2).to);
            assertEquals(1500.0, a.getItemAt(2).dist, DOUBLE_EPSILON);
            assertEquals(View.INVISIBLE, pb.getVisibility());
            assertEquals(View.VISIBLE, nb.getVisibility());

            nb.performClick();

            assertEquals("Directions from Flamingos to Capuchin Monkeys", t.getText());
            assertEquals(1, rv.getAdapter().getItemCount());
            assertEquals("Flamingos", a.getItemAt(0).from);
            assertEquals("Monkey Trail", a.getItemAt(0).street);
            assertEquals("Capuchin Monkeys", a.getItemAt(0).to);
            assertEquals(3100.0, a.getItemAt(0).dist, DOUBLE_EPSILON);
            assertEquals(View.VISIBLE, pb.getVisibility());
            assertEquals(View.INVISIBLE, nb.getVisibility());
        });
    }

}
