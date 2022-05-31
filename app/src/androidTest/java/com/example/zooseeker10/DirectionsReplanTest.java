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
public class DirectionsReplanTest {

    private static Graph<String, IdentifiedWeightedEdge> graph;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();

        graph = ZooData.getZooGraph(context);
    }

    @Test
    public void testReplan() {
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
            // simulate movement & replan
            activity.handleLocationChanged(new LatLng(32.735851415, -117.162894167));
            // assertTrue(activity.userTracker.needsReplan());
            activity.onReplanRequested();

            // parker_aviary -> hippo -> capuchin -> flamingo -> end
            assertEquals(5, activity.plan.size());
            assertEquals("siamang", activity.plan.plan.get(0).getStartVertex());
            assertEquals("parker_aviary", activity.plan.plan.get(0).getEndVertex());
            assertEquals("parker_aviary", activity.plan.plan.get(1).getStartVertex());
            assertEquals("hippo", activity.plan.plan.get(1).getEndVertex());
            assertEquals("hippo", activity.plan.plan.get(2).getStartVertex());
            assertEquals("capuchin", activity.plan.plan.get(2).getEndVertex());
            assertEquals("capuchin", activity.plan.plan.get(3).getStartVertex());
            assertEquals("flamingo", activity.plan.plan.get(3).getEndVertex());
            assertEquals("flamingo", activity.plan.plan.get(4).getStartVertex());
            assertEquals("entrance_exit_gate", activity.plan.plan.get(4).getEndVertex());

            activity.findViewById(R.id.directions_next_button).performClick();
            activity.findViewById(R.id.directions_next_button).performClick();
            activity.handleLocationChanged(new LatLng(32.736951532, -117.159366787));
            // assertTrue(activity.userTracker.needsReplan());
            activity.onReplanRequested();

            // parker_aviary -> hippo -> flamingo -> capuchin -> end
            assertEquals(5, activity.plan.size());
            // start vertices omitted because updated by current location etc
            assertEquals("parker_aviary", activity.plan.plan.get(0).getEndVertex());
            assertEquals("hippo", activity.plan.plan.get(1).getEndVertex());
            assertEquals("intxn_treetops_orangutan_trail", activity.plan.plan.get(2).getStartVertex());
            assertEquals("flamingo", activity.plan.plan.get(2).getEndVertex());
            assertEquals("flamingo", activity.plan.plan.get(3).getStartVertex());
            assertEquals("capuchin", activity.plan.plan.get(3).getEndVertex());
            assertEquals("capuchin", activity.plan.plan.get(4).getStartVertex());
            assertEquals("entrance_exit_gate", activity.plan.plan.get(4).getEndVertex());

            // activity.nextButton.performClick();
            // activity.nextButton.performClick();
            // assertFalse(activity.userTracker.needsReplan());
        });
    }
}
