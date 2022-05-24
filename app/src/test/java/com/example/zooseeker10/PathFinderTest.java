package com.example.zooseeker10;

import org.junit.*;
import static org.junit.Assert.*;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class PathFinderTest {
    public static final double DOUBLE_EPSILON = 1e-7;
    private static PathFinder pathfinder;

    /**
     * Imports the graph topology from the example file
     */
    @Before
    public void setup() {
        Graph<String, IdentifiedWeightedEdge> graph = ZooData.loadZooGraphJSON(ApplicationProvider.getApplicationContext(), ZooData.ZOO_GRAPH_PATH);

        pathfinder = new PathFinder(graph, ZooData.ENTRANCE_GATE_ID, ZooData.EXIT_GATE_ID);
    }

    private static List<String> getIDs(List<IdentifiedWeightedEdge> edgeList) {
        List<String> ids = new ArrayList<>();
        for (IdentifiedWeightedEdge e : edgeList) {
            ids.add(e.getId());
        }
        return ids;
    }

    @Test
    public void findPath_noTargets() {
        ZooPlan plan = pathfinder.findPath(Arrays.asList());
        Iterator<GraphPath<String, IdentifiedWeightedEdge>> planIterator = plan.iterator();
        GraphPath<String, IdentifiedWeightedEdge> planPart = planIterator.next();
        assertEquals(Arrays.asList(), planPart.getEdgeList());
        assertEquals(Arrays.asList("entrance_exit_gate"), planPart.getVertexList());
        assertEquals(0.0, planPart.getWeight(), DOUBLE_EPSILON);
        assertFalse(planIterator.hasNext());
    }

    @Test
    public void findPath_oneTarget() {
        ZooPlan plan = pathfinder.findPath(Arrays.asList("gorillas"));
        Iterator<GraphPath<String, IdentifiedWeightedEdge>> planIterator = plan.iterator();
        GraphPath<String, IdentifiedWeightedEdge> planPart = planIterator.next();
        assertEquals(Arrays.asList("edge-0", "edge-1"), getIDs(planPart.getEdgeList()));
        assertEquals(Arrays.asList("entrance_exit_gate", "entrance_plaza", "gorillas"), planPart.getVertexList());
        assertEquals(210.0, planPart.getWeight(), DOUBLE_EPSILON);
        planPart = planIterator.next();
        assertEquals(Arrays.asList("edge-1", "edge-0"), getIDs(planPart.getEdgeList()));
        assertEquals(Arrays.asList("gorillas", "entrance_plaza", "entrance_exit_gate"), planPart.getVertexList());
        assertEquals(210.0, planPart.getWeight(), DOUBLE_EPSILON);
        assertFalse(planIterator.hasNext());
    }

    @Test
    public void findPath_twoTargets() {
        ZooPlan plan = pathfinder.findPath(Arrays.asList("gorillas", "arctic_foxes"));
        Iterator<GraphPath<String, IdentifiedWeightedEdge>> planIterator = plan.iterator();
        GraphPath<String, IdentifiedWeightedEdge> planPart = planIterator.next();
        assertEquals(Arrays.asList("edge-0", "edge-1"), getIDs(planPart.getEdgeList()));
        assertEquals(Arrays.asList("entrance_exit_gate", "entrance_plaza", "gorillas"), planPart.getVertexList());
        assertEquals(210.0, planPart.getWeight(), DOUBLE_EPSILON);
        planPart = planIterator.next();
        assertEquals(Arrays.asList("edge-1", "edge-4"), getIDs(planPart.getEdgeList()));
        assertEquals(Arrays.asList("gorillas", "entrance_plaza", "arctic_foxes"), planPart.getVertexList());
        assertEquals(500.0, planPart.getWeight(), DOUBLE_EPSILON);
        planPart = planIterator.next();
        assertEquals(Arrays.asList("edge-4", "edge-0"), getIDs(planPart.getEdgeList()));
        assertEquals(Arrays.asList("arctic_foxes", "entrance_plaza", "entrance_exit_gate"), planPart.getVertexList());
        assertEquals(310.0, planPart.getWeight(), DOUBLE_EPSILON);
        assertFalse(planIterator.hasNext());
    }

    @Test
    public void findPath_allTargets() {
        ZooPlan plan = pathfinder.findPath(Arrays.asList("arctic_foxes", "elephant_odyssey", "gators", "gorillas", "lions"));
        Iterator<GraphPath<String, IdentifiedWeightedEdge>> planIterator = plan.iterator();
        GraphPath<String, IdentifiedWeightedEdge> planPart = planIterator.next();
        assertEquals(Arrays.asList("edge-0", "edge-5"), getIDs(planPart.getEdgeList()));
        assertEquals(Arrays.asList("entrance_exit_gate", "entrance_plaza", "gators"), planPart.getVertexList());
        assertEquals(110.0, planPart.getWeight(), DOUBLE_EPSILON);
        planPart = planIterator.next();
        assertEquals(Arrays.asList("edge-6"), getIDs(planPart.getEdgeList()));
        assertEquals(Arrays.asList("gators", "lions"), planPart.getVertexList());
        assertEquals(200.0, planPart.getWeight(), DOUBLE_EPSILON);
        planPart = planIterator.next();
        assertEquals(Arrays.asList("edge-3"), getIDs(planPart.getEdgeList()));
        assertEquals(Arrays.asList("lions", "elephant_odyssey"), planPart.getVertexList());
        assertEquals(200.0, planPart.getWeight(), DOUBLE_EPSILON);
        planPart = planIterator.next();
        assertEquals(Arrays.asList("edge-3", "edge-2"), getIDs(planPart.getEdgeList()));
        assertEquals(Arrays.asList("elephant_odyssey", "lions", "gorillas"), planPart.getVertexList());
        assertEquals(400.0, planPart.getWeight(), DOUBLE_EPSILON);
        planPart = planIterator.next();
        assertEquals(Arrays.asList("edge-1", "edge-4"), getIDs(planPart.getEdgeList()));
        assertEquals(Arrays.asList("gorillas", "entrance_plaza", "arctic_foxes"), planPart.getVertexList());
        assertEquals(500.0, planPart.getWeight(), DOUBLE_EPSILON);
        planPart = planIterator.next();
        assertEquals(Arrays.asList("edge-4", "edge-0"), getIDs(planPart.getEdgeList()));
        assertEquals(Arrays.asList("arctic_foxes", "entrance_plaza", "entrance_exit_gate"), planPart.getVertexList());
        assertEquals(310.0, planPart.getWeight(), DOUBLE_EPSILON);
        assertFalse(planIterator.hasNext());
    }

    @Test
    public void findPathFrom() {
        ZooPlan plan = pathfinder.findPath(Arrays.asList("gorillas", "lions"), "elephant_odyssey");
        Iterator<GraphPath<String, IdentifiedWeightedEdge>> planIterator = plan.iterator();
        GraphPath<String, IdentifiedWeightedEdge> planPart = planIterator.next();
        assertEquals(Arrays.asList("edge-3"), getIDs(planPart.getEdgeList()));
        assertEquals(Arrays.asList("elephant_odyssey", "lions"), planPart.getVertexList());
        assertEquals(200.0, planPart.getWeight(), DOUBLE_EPSILON);
        planPart = planIterator.next();
        assertEquals(Arrays.asList("edge-2"), getIDs(planPart.getEdgeList()));
        assertEquals(Arrays.asList("lions", "gorillas"), planPart.getVertexList());
        assertEquals(200.0, planPart.getWeight(), DOUBLE_EPSILON);
        planPart = planIterator.next();
        assertEquals(Arrays.asList("edge-1", "edge-0"), getIDs(planPart.getEdgeList()));
        assertEquals(Arrays.asList("gorillas", "entrance_plaza", "entrance_exit_gate"), planPart.getVertexList());
        assertEquals(210.0, planPart.getWeight(), DOUBLE_EPSILON);
        assertFalse(planIterator.hasNext());
    }
}
