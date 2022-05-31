package com.example.zooseeker10;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

import com.google.android.gms.maps.model.LatLng;

import org.jgrapht.Graph;
import org.jgrapht.graph.GraphWalk;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

@RunWith(AndroidJUnit4.class)
public class SkipExhibitTest {

    private static Graph<String, IdentifiedWeightedEdge> graph;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        graph = ZooData.getZooGraph(context);
    }

    @Test
    public void testSkipAllExhibits() {
        ZooPlan plan = new ZooPlan(Arrays.asList(
                new GraphWalk<>(graph, Arrays.asList("entrance_exit_gate", "intxn_front_treetops", "intxn_front_monkey", "flamingo"), 5300.0),
                new GraphWalk<>(graph, Arrays.asList("flamingo", "capuchin"), 3100.0),
                new GraphWalk<>(graph, Arrays.asList("capuchin", "flamingo_to_capuchin", "intxn_front_monkey", "intxn_front_treetops", "entrance_exit_gate"), 8400.0)
        ));
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DirectionsActivity.class);
        intent.putExtra("paths", plan);
        ActivityScenario<DirectionsActivity> scenario
                = ActivityScenario.launch(intent);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            Button skipButton = activity.findViewById(R.id.skip_button);
            Button nextButton = activity.findViewById(R.id.directions_next_button);
            Button previousButton = activity.findViewById(R.id.directions_previous_button);
            RecyclerView rv = activity.findViewById(R.id.directions_list);
            DirectionsListAdapter dlAdapter = (DirectionsListAdapter) rv.getAdapter();
            TextView tv = activity.findViewById(R.id.directions_title);

            assertEquals(3, activity.plan.size());
            assertEquals("Directions from Entrance and Exit Gate to Flamingos", tv.getText());
            assertEquals(3, dlAdapter.getItemCount());

            skipButton.performClick();

            assertEquals(2, activity.plan.size());
            activity.lastVertexLocation = activity.userTracker.getClosestVertex().name;
            assertEquals("Directions from " + activity.lastVertexLocation + " to Capuchin Monkeys", tv.getText());
            assertEquals(View.INVISIBLE, previousButton.getVisibility());
            assertEquals(View.VISIBLE, skipButton.getVisibility());

            skipButton.performClick();

            assertEquals(1, activity.plan.size());
            activity.lastVertexLocation = activity.userTracker.getClosestVertex().name;
            assertEquals("Directions from " + activity.lastVertexLocation + " to Entrance and Exit Gate", tv.getText());
            assertEquals(View.INVISIBLE, nextButton.getVisibility());
            assertEquals(View.INVISIBLE, previousButton.getVisibility());
            assertEquals(View.INVISIBLE, skipButton.getVisibility());
        });
    }

    @Test
    public void testSkipOneExhibit() {
        ZooPlan plan = new ZooPlan(Arrays.asList(
                new GraphWalk<>(graph, Arrays.asList("entrance_exit_gate", "intxn_front_treetops", "intxn_front_monkey", "flamingo"), 5300.0),
                new GraphWalk<>(graph, Arrays.asList("flamingo", "capuchin"), 3100.0),
                new GraphWalk<>(graph, Arrays.asList("capuchin", "flamingo_to_capuchin", "intxn_front_monkey", "intxn_front_treetops", "entrance_exit_gate"), 8400.0)
        ));
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DirectionsActivity.class);
        intent.putExtra("paths", plan);
        ActivityScenario<DirectionsActivity> scenario
                = ActivityScenario.launch(intent);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            Button skipButton = activity.findViewById(R.id.skip_button);
            Button nextButton = activity.findViewById(R.id.directions_next_button);
            Button previousButton = activity.findViewById(R.id.directions_previous_button);
            RecyclerView rv = activity.findViewById(R.id.directions_list);
            DirectionsListAdapter dlAdapter = (DirectionsListAdapter) rv.getAdapter();
            TextView tv = activity.findViewById(R.id.directions_title);

            assertEquals(3, activity.plan.size());
            assertEquals("Directions from Entrance and Exit Gate to Flamingos", tv.getText());
            assertEquals(3, dlAdapter.getItemCount());

            nextButton.performClick();

            activity.lastVertexLocation = activity.userTracker.getClosestVertex().name;
            assertEquals("Directions from Flamingos to Capuchin Monkeys", tv.getText());
            assertEquals(View.VISIBLE, previousButton.getVisibility());
            assertEquals(View.VISIBLE, skipButton.getVisibility());

            skipButton.performClick();

            assertEquals(2, activity.plan.size());
            activity.lastVertexLocation = activity.userTracker.getClosestVertex().name;
            assertEquals("Directions from " + activity.lastVertexLocation + " to Entrance and Exit Gate", tv.getText());
            assertEquals(View.INVISIBLE, nextButton.getVisibility());
            assertEquals(View.VISIBLE, previousButton.getVisibility());
            assertEquals(View.INVISIBLE, skipButton.getVisibility());
        });
    }
}
