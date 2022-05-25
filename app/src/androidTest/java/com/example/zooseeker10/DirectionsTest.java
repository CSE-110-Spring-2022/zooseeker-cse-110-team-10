package com.example.zooseeker10;

import static org.junit.Assert.assertEquals;

import android.content.Context;
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
        Context context = ApplicationProvider.getApplicationContext();

        graph = ZooData.getZooGraph(context);
    }

    @Test
    public void testDirections() {
        ZooPlan plan = new ZooPlan(Arrays.asList(
                new GraphWalk<>(graph, Arrays.asList("entrance_exit_gate", "intxn_front_treetops", "intxn_front_monkey", "flamingo"), 5300.0)
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
            assertEquals("Directions from Entrance and Exit Gate to Flamingos", t.getText());
            RecyclerView rv = activity.findViewById(R.id.directions_list);
            assertEquals(3, rv.getAdapter().getItemCount());
            RecyclerView.ViewHolder vh0 = rv.findViewHolderForAdapterPosition(0);
            assertEquals("From Entrance and Exit Gate", ((TextView)vh0.itemView.findViewById(R.id.directions_from_text)).getText());
            assertEquals("Along Gate Path", ((TextView)vh0.itemView.findViewById(R.id.directions_along_text)).getText());
            assertEquals("To Front Street / Treetops Way", ((TextView)vh0.itemView.findViewById(R.id.directions_to_text)).getText());
            assertEquals("1100.0 ft", ((TextView)vh0.itemView.findViewById(R.id.directions_distance_text)).getText());
            RecyclerView.ViewHolder vh1 = rv.findViewHolderForAdapterPosition(1);
            assertEquals("From Front Street / Treetops Way", ((TextView)vh1.itemView.findViewById(R.id.directions_from_text)).getText());
            assertEquals("Along Front Street", ((TextView)vh1.itemView.findViewById(R.id.directions_along_text)).getText());
            assertEquals("To Front Street / Monkey Trail", ((TextView)vh1.itemView.findViewById(R.id.directions_to_text)).getText());
            assertEquals("2700.0 ft", ((TextView)vh1.itemView.findViewById(R.id.directions_distance_text)).getText());
            RecyclerView.ViewHolder vh2 = rv.findViewHolderForAdapterPosition(2);
            assertEquals("From Front Street / Monkey Trail", ((TextView)vh2.itemView.findViewById(R.id.directions_from_text)).getText());
            assertEquals("Along Monkey Trail", ((TextView)vh2.itemView.findViewById(R.id.directions_along_text)).getText());
            assertEquals("To Flamingos", ((TextView)vh2.itemView.findViewById(R.id.directions_to_text)).getText());
            assertEquals("1500.0 ft", ((TextView)vh2.itemView.findViewById(R.id.directions_distance_text)).getText());
        });
    }

}
