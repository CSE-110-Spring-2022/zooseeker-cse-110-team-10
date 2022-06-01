package com.example.zooseeker10;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.Graph;
import org.jgrapht.graph.GraphWalk;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class RetainStateTest {

    StateManager stateManager;

    @Before
    public void setup() {
        stateManager = StateManager.getSingleton(ApplicationProvider.getApplicationContext());

        Context context = ApplicationProvider.getApplicationContext();
        File directory = context.getDir(Globals.State.DIRECTORY_PATH, Context.MODE_PRIVATE);

         File stateFile = new File(directory, Globals.State.ACTIVE_STATE_FILENAME);
         File selectionFile = new File(directory, Globals.State.SELECTION_FILENAME);
         File planFile = new File(directory, Globals.State.PLAN_FILENAME);
         File directionsFile = new File(directory, Globals.State.DIRECTIONS_FILENAME);

         stateFile.delete();
         selectionFile.delete();
         planFile.delete();
         directionsFile.delete();
    }

    private static List<String> getIDs(List<IdentifiedWeightedEdge> edgeList) {
        List<String> ids = new ArrayList<>();
        for (IdentifiedWeightedEdge e : edgeList) {
            ids.add(e.getId());
        }
        return ids;
    }

    @Test
    public void testGetSelectionState() {

        ActivityScenario<TrampolineActivity> scenario
                = ActivityScenario.launch(TrampolineActivity.class);
        // scenario.moveToState(Lifecycle.State.CREATED);
        // scenario.moveToState(Lifecycle.State.STARTED);
        // scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            SelectedExhibits s = new SelectedExhibits(ApplicationProvider.getApplicationContext(), () -> {});
            s.addExhibit("gorilla");
            s.addExhibit("flamingo");
            s.addExhibit("koi");
            stateManager.storeSelectionState(s);
            Intent intent = activity.loadIntentFromFile();
            assertEquals("com.example.zooseeker10.SelectionActivity", intent.getComponent().getClassName());
            ArrayList<String> s2 = intent.getStringArrayListExtra(Globals.MapKeys.SELECTED_EXHIBIT_IDS);
            assertEquals(Arrays.asList("koi", "flamingo", "gorilla"), s2);
        });
    }

    @Test
    public void testGetPlanState() {
        ActivityScenario<TrampolineActivity> scenario
                = ActivityScenario.launch(TrampolineActivity.class);
        // scenario.moveToState(Lifecycle.State.CREATED);
        // scenario.moveToState(Lifecycle.State.STARTED);
        // scenario.moveToState(Lifecycle.State.RESUMED);

        Graph graph = ZooData.getZooGraph(ApplicationProvider.getApplicationContext());

        scenario.onActivity(activity -> {
            ZooPlan p = new ZooPlan(Arrays.asList(
                    new GraphWalk<>(graph, Arrays.asList("entrance_exit_gate", "intxn_front_treetops", "intxn_front_monkey", "flamingo"), 5300.0),
                    new GraphWalk<>(graph, Arrays.asList("flamingo", "capuchin"), 3100.0)
            ));
            stateManager.storePlanState(p);
            Intent intent = activity.loadIntentFromFile();
            assertEquals("com.example.zooseeker10.PlanActivity", intent.getComponent().getClassName());
            ZooPlan p2 = (ZooPlan) intent.getSerializableExtra(Globals.MapKeys.ZOOPLAN);
            assertEquals(p2.size(), 2);
            assertEquals(Arrays.asList("entrance_exit_gate", "intxn_front_treetops", "intxn_front_monkey", "flamingo"), p2.plan.get(0).getVertexList());
            assertEquals(Arrays.asList("gate_to_front", "front_to_monkey", "monkey_to_flamingo"), getIDs(p2.plan.get(0).getEdgeList()));
            assertEquals(Arrays.asList("flamingo", "capuchin"), p2.plan.get(1).getVertexList());
            assertEquals(Arrays.asList("flamingo_to_capuchin"), getIDs(p2.plan.get(1).getEdgeList()));
        });
    }

    @Test
    public void testGetDirectionsState() {
        ActivityScenario<TrampolineActivity> scenario
                = ActivityScenario.launch(TrampolineActivity.class);
        // scenario.moveToState(Lifecycle.State.CREATED);
        // scenario.moveToState(Lifecycle.State.STARTED);
        // scenario.moveToState(Lifecycle.State.RESUMED);

        Graph graph = ZooData.getZooGraph(ApplicationProvider.getApplicationContext());

        scenario.onActivity(activity -> {
            ZooPlan p = new ZooPlan(Arrays.asList(
                    new GraphWalk<>(graph, Arrays.asList("entrance_exit_gate", "intxn_front_treetops", "intxn_front_monkey", "flamingo"), 5300.0),
                    new GraphWalk<>(graph, Arrays.asList("flamingo", "capuchin"), 3100.0)
            ));
            stateManager.storeDirectionsState(p, 1);
            Intent intent = activity.loadIntentFromFile();
            assertEquals("com.example.zooseeker10.DirectionsActivity", intent.getComponent().getClassName());
            ZooPlan p2 = (ZooPlan) intent.getSerializableExtra(Globals.MapKeys.ZOOPLAN);
            assertEquals(p2.size(), 2);
            assertEquals(Arrays.asList("entrance_exit_gate", "intxn_front_treetops", "intxn_front_monkey", "flamingo"), p2.plan.get(0).getVertexList());
            assertEquals(Arrays.asList("gate_to_front", "front_to_monkey", "monkey_to_flamingo"), getIDs(p2.plan.get(0).getEdgeList()));
            assertEquals(Arrays.asList("flamingo", "capuchin"), p2.plan.get(1).getVertexList());
            assertEquals(Arrays.asList("flamingo_to_capuchin"), getIDs(p2.plan.get(1).getEdgeList()));
            assertEquals(1, intent.getIntExtra(Globals.MapKeys.WALKER_INDEX, -1));
        });
    }
}
