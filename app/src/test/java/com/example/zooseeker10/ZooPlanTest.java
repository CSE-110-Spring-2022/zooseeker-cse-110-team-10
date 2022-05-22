package com.example.zooseeker10;

import static org.junit.Assert.*;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.Graph;
import org.jgrapht.graph.GraphWalk;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ZooPlanTest {
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
    public void summarizePath_noExhibits() {
        ZooPlan plan = new ZooPlan(Arrays.asList(
                new GraphWalk<>(graph, Arrays.asList("entrance_exit_gate"), 0.0)
        ));
        List<PlanDistItem> summary = plan.summarizePath(ApplicationProvider.getApplicationContext());
        assertEquals(1, summary.size());
        assertEquals("Entrance and Exit Gate", summary.get(0).exhibitName);
        assertEquals(0.0, summary.get(0).distance, DOUBLE_EPSILON);
    }

    @Test
    public void summarizePath_oneExhibit() {
        ZooPlan plan = new ZooPlan(Arrays.asList(
                new GraphWalk<>(graph, Arrays.asList("entrance_exit_gate", "entrance_plaza", "gorillas"), 210.0),
                new GraphWalk<>(graph, Arrays.asList("gorillas", "entrance_plaza", "entrance_exit_gate"), 210.0)
        ));
        List<PlanDistItem> summary = plan.summarizePath(ApplicationProvider.getApplicationContext());
        assertEquals(2, summary.size());
        assertEquals("Gorillas", summary.get(0).exhibitName);
        assertEquals(210.0, summary.get(0).distance, DOUBLE_EPSILON);
        assertEquals("Entrance and Exit Gate", summary.get(1).exhibitName);
        assertEquals(420.0, summary.get(1).distance, DOUBLE_EPSILON);
    }

    @Test
    public void summarizePath_manyExhibits() {
        ZooPlan plan = new ZooPlan(Arrays.asList(
                new GraphWalk<>(graph, Arrays.asList("entrance_exit_gate", "entrance_plaza", "gorillas"), 210.0),
                new GraphWalk<>(graph, Arrays.asList("gorillas", "lions"), 200.0),
                new GraphWalk<>(graph, Arrays.asList("lions", "gators"), 200.0),
                new GraphWalk<>(graph, Arrays.asList("gators", "entrance_plaza", "entrance_exit_gate"), 110.0)
        ));
        List<PlanDistItem> summary = plan.summarizePath(ApplicationProvider.getApplicationContext());
        assertEquals(4, summary.size());
        assertEquals("Gorillas", summary.get(0).exhibitName);
        assertEquals(210.0, summary.get(0).distance, DOUBLE_EPSILON);
        assertEquals("Lions", summary.get(1).exhibitName);
        assertEquals(410.0, summary.get(1).distance, DOUBLE_EPSILON);
        assertEquals("Alligators", summary.get(2).exhibitName);
        assertEquals(610.0, summary.get(2).distance, DOUBLE_EPSILON);
        assertEquals("Entrance and Exit Gate", summary.get(3).exhibitName);
        assertEquals(720.0, summary.get(3).distance, DOUBLE_EPSILON);
    }

    @Test
    public void explainPath_oneEdge() {
        ZooPlan plan = new ZooPlan(Arrays.asList(
                new GraphWalk<>(graph, Arrays.asList("gorillas", "entrance_plaza"), 200.0),
                new GraphWalk<>(graph, Arrays.asList("entrance_plaza", "arctic_foxes"), 300.0)
        ));
        ZooPlan.ZooWalker zw = plan.new ZooWalker(0);
        List<DirectionsItem> explain = zw.explainPath(ApplicationProvider.getApplicationContext());
        assertEquals(1, explain.size());
        assertEquals("Gorillas", explain.get(0).from);
        assertEquals("Entrance Plaza", explain.get(0).to);
        assertEquals("Africa Rocks Street", explain.get(0).street);
        assertEquals(200.0, explain.get(0).dist, DOUBLE_EPSILON);
        zw.traverseForward();
        explain = zw.explainPath(ApplicationProvider.getApplicationContext());
        assertEquals(1, explain.size());
        assertEquals("Entrance Plaza", explain.get(0).from);
        assertEquals("Arctic Foxes", explain.get(0).to);
        assertEquals("Arctic Avenue", explain.get(0).street);
        assertEquals(300.0, explain.get(0).dist, DOUBLE_EPSILON);
    }

    @Test
    public void explainPath_multipleEdges() {
        ZooPlan plan = new ZooPlan(Arrays.asList(
                new GraphWalk<>(graph, Arrays.asList("entrance_exit_gate", "entrance_plaza", "gators", "lions"), 310.0)
        ));
        ZooPlan.ZooWalker zw = plan.new ZooWalker(0);
        List<DirectionsItem> explain = zw.explainPath(ApplicationProvider.getApplicationContext());
        assertEquals(3, explain.size());
        assertEquals("Entrance and Exit Gate", explain.get(0).from);
        assertEquals("Entrance Plaza", explain.get(0).to);
        assertEquals("Entrance Way", explain.get(0).street);
        assertEquals(10.0, explain.get(0).dist, DOUBLE_EPSILON);
        assertEquals("Entrance Plaza", explain.get(1).from);
        assertEquals("Alligators", explain.get(1).to);
        assertEquals("Reptile Road", explain.get(1).street);
        assertEquals(100.0, explain.get(1).dist, DOUBLE_EPSILON);
        assertEquals("Alligators", explain.get(2).from);
        assertEquals("Lions", explain.get(2).to);
        assertEquals("Sharp Teeth Shortcut", explain.get(2).street);
        assertEquals(200.0, explain.get(2).dist, DOUBLE_EPSILON);
    }
}
