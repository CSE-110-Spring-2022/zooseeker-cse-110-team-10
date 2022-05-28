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
    public void testBriefDetailedDirections(){
        ZooPlan plan = pathfinder.findPath(Arrays.asList("gorilla"));
        List<DirectionsItem> briefList = plan.startWalker().explainPath(context, true);

        assertEquals(4, briefList.size());

        assertEquals("Entrance and Exit Gate", briefList.get(0).from);
        assertEquals("Front Street / Treetops Way", briefList.get(0).to);
        assertEquals("Gate Path", briefList.get(0).street);
        assertEquals(1100.0, briefList.get(0).dist, DOUBLE_EPSILON);

        assertEquals("Front Street / Treetops Way", briefList.get(1).from);
        assertEquals("Treetops Way / Hippo Trail", briefList.get(1).to);
        assertEquals("Treetops Way", briefList.get(1).street);
        assertEquals(4400.0, briefList.get(1).dist, DOUBLE_EPSILON);

        assertEquals("Treetops Way / Hippo Trail", briefList.get(2).from);
        assertEquals("Monkey Trail / Hippo Trail", briefList.get(2).to);
        assertEquals("Hippo Trail", briefList.get(2).street);
        assertEquals(4500.0, briefList.get(2).dist, DOUBLE_EPSILON);

        assertEquals("Monkey Trail / Hippo Trail", briefList.get(3).from);
        assertEquals("Gorillas", briefList.get(3).to);
        assertEquals("Monkey Trail", briefList.get(3).street);
        assertEquals(2400.0, briefList.get(3).dist, DOUBLE_EPSILON);
    }

    /*
    @Test
    public void switchFromDetailedToBriefDirectionsType(){
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
            //activity.isBriefDirections=true;
            //activity.updateDirectionsType();

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

    @Test
    public void switchFromBriefToDetailedDirectionsType(){
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
            activity.isBriefDirections=false;
            activity.updateDirectionsType();

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
     */
}
