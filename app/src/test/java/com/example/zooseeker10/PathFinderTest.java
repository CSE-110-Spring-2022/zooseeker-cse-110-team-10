package com.example.zooseeker10;

import org.junit.*;
import static org.junit.Assert.*;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PathFinderTest {
    private static final String GRAPH_INFO_JSON_PATH = "sample_zoo_graph.json";
    private static final String START_VERTEX_ID = "entrance_exit_gate";
    private static final String END_VERTEX_ID = "entrance_exit_gate";
    private static final double DOUBLE_EPSILON = 1e-15;

    private static PathFinder pathfinder;
    private static List<String> verticesToVisit;

    /**
     * Imports the graph topology from the example file
     */
    @Before
    public void setup() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(GRAPH_INFO_JSON_PATH);
        Reader reader = new InputStreamReader(inputStream);
        Graph<String, IdentifiedWeightedEdge> graph = ZooData.loadZooGraphJSON(reader);

        pathfinder = new PathFinder(graph, START_VERTEX_ID, END_VERTEX_ID);
    }

    public void checkPathSanity(String tag, List<String> toVisit, List<GraphPath<String, IdentifiedWeightedEdge>> path, double maxWeight) {
        assertEquals(tag + ": wrong number of subpaths", toVisit.size() + 1, path.size());
        ArrayList<GraphPath<String, IdentifiedWeightedEdge>> pathArr = new ArrayList<>(path);
        HashSet<String> vertices = new HashSet<>(toVisit);
        for (int i = 0; i < pathArr.size() - 1; i++) {
            String exhibit = pathArr.get(i).getStartVertex();
            assertEquals(tag + ": path disconnected", exhibit, pathArr.get(i+1).getEndVertex());
            assertFalse(tag + ": duplicated exhibit", vertices.contains(exhibit));
            vertices.remove(exhibit);
        }
        assertEquals(tag + ": wrong start", START_VERTEX_ID, pathArr.get(0).getStartVertex());
        assertEquals(tag + ": wrong end", END_VERTEX_ID, pathArr.get(pathArr.size() - 1).getEndVertex());
        double actualWeight = 0;
        for (GraphPath<String, IdentifiedWeightedEdge> subPath : path) { actualWeight += subPath.getWeight(); };
        assertTrue(String.format("%s: weight too large (got %f, required %f)", tag, actualWeight, maxWeight), actualWeight <= maxWeight);
    }

    @Test
    public void findPath_noTargets() {
        verticesToVisit = new ArrayList<>();
        List<GraphPath<String, IdentifiedWeightedEdge>> calculatedPath = pathfinder.findPath(verticesToVisit);
        checkPathSanity("no targets", verticesToVisit, calculatedPath, 0);
    }
}
