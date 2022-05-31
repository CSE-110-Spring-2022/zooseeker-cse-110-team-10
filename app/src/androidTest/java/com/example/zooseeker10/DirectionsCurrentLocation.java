package com.example.zooseeker10;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import org.jgrapht.Graph;
import org.jgrapht.graph.GraphWalk;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

@RunWith(AndroidJUnit4.class)
public class DirectionsCurrentLocation {

    private static Graph<String, IdentifiedWeightedEdge> graph;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();

        graph = ZooData.getZooGraph(context);
    }

    @Test
    public void testCurrentLocation() {
        // flamingo -> capuchin -> hippo -> parker_aviary -> end
        ZooPlan plan = new ZooPlan(Arrays.asList(
                new GraphWalk<>(graph, Arrays.asList("entrance_exit_gate", "intxn_front_treetops", "intxn_front_monkey", "flamingo"), 5300.0),
                new GraphWalk<>(graph, Arrays.asList("flamingo", "capuchin"), 3100.0),
                new GraphWalk<>(graph, Arrays.asList("capuchin", "intxn_hippo_monkey_trails", "crocodile", "hippo"), 4900.0),
                new GraphWalk<>(graph, Arrays.asList("hippo", "intxn_treetops_hippo_trail", "parker_aviary"), 4200.0),
                new GraphWalk<>(graph, Arrays.asList("parker_aviary", "orangutan", "siamang", "intxn_treetops_orangutan_trail", "intxn_treetops_fern_trail", "intxn_front_treetops", "entrance_exit_gate"), 7400.0)
        ));
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DirectionsActivity.class);
        intent.putExtra("paths", plan);
        ActivityScenario<DirectionsActivity> scenario
                = ActivityScenario.launch(intent);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView rv = activity.findViewById(R.id.directions_list);
            DirectionsListAdapter a = (DirectionsListAdapter)rv.getAdapter();

            assertEquals("Entrance and Exit Gate", a.getItemAt(0).from);
            assertEquals("Front Street / Treetops Way", a.getItemAt(0).to);
            assertEquals("Front Street / Treetops Way", a.getItemAt(1).from);
            assertEquals("Front Street / Monkey Trail", a.getItemAt(1).to);
            assertEquals("Front Street / Monkey Trail", a.getItemAt(2).from);
            assertEquals("Flamingos", a.getItemAt(2).to);
            assertEquals(3, a.getItemCount());
            activity.handleLocationChanged(new LatLng(32.747975695, -117.173220082));
            assertEquals("Monkey Trail / Hippo Trail", a.getItemAt(0).from);
            assertEquals("Capuchin Monkeys", a.getItemAt(0).to);
            assertEquals("Capuchin Monkeys", a.getItemAt(1).from);
            assertEquals("Flamingos", a.getItemAt(1).to);
            assertEquals(2, a.getItemCount());

            activity.findViewById(R.id.directions_next_button).performClick();
            activity.findViewById(R.id.directions_next_button).performClick();
            activity.handleLocationChanged(new LatLng(32.744761202, -117.183699732));
            activity.findViewById(R.id.directions_next_button).performClick();

            assertEquals("Benchley Plaza", a.getItemAt(0).from);
            assertEquals("Parker Aviary", a.getItemAt(0).to);
            assertEquals(1, a.getItemCount());
        });
    }
}
