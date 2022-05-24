package com.example.zooseeker10;

import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.view.View;
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
public class DirectionsTest {

    private static Graph<String, IdentifiedWeightedEdge> graph;

    /**
     * Imports the graph topology from the example file
     */
    @Before
    public void setup() {
        graph = ZooData.loadZooGraphJSON(ApplicationProvider.getApplicationContext(), ZooData.ZOO_GRAPH_PATH);
    }

    @Test
    public void testDirections() {
        ZooPlan plan = new ZooPlan(Arrays.asList(
                new GraphWalk<>(graph, Arrays.asList("entrance_exit_gate", "entrance_plaza", "arctic_foxes"), 310.0)
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
            assertEquals("Directions from Entrance and Exit Gate to Arctic Foxes", t.getText());
            RecyclerView rv = activity.findViewById(R.id.directions_list);
            assertEquals(2, rv.getAdapter().getItemCount());
            RecyclerView.ViewHolder vh0 = rv.findViewHolderForAdapterPosition(0);
            assertEquals("From Entrance and Exit Gate", ((TextView)vh0.itemView.findViewById(R.id.directions_from_text)).getText());
            assertEquals("Along Entrance Way", ((TextView)vh0.itemView.findViewById(R.id.directions_along_text)).getText());
            assertEquals("To Entrance Plaza", ((TextView)vh0.itemView.findViewById(R.id.directions_to_text)).getText());
            assertEquals("10.0 ft", ((TextView)vh0.itemView.findViewById(R.id.directions_distance_text)).getText());
            RecyclerView.ViewHolder vh1 = rv.findViewHolderForAdapterPosition(1);
            assertEquals("From Entrance Plaza", ((TextView)vh1.itemView.findViewById(R.id.directions_from_text)).getText());
            assertEquals("Along Arctic Avenue", ((TextView)vh1.itemView.findViewById(R.id.directions_along_text)).getText());
            assertEquals("To Arctic Foxes", ((TextView)vh1.itemView.findViewById(R.id.directions_to_text)).getText());
            assertEquals("300.0 ft", ((TextView)vh1.itemView.findViewById(R.id.directions_distance_text)).getText());
        });
    }

}
