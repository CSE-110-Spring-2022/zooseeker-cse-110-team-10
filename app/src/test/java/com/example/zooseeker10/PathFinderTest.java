package com.example.zooseeker10;

import org.junit.*;
import static org.junit.Assert.*;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class PathFinderTest {
    private static final String GRAPH_INFO_JSON_PATH = "sample_zoo_graph.json";
    private static final String START_VERTEX_ID = "entrance_exit_gate";
    private static final String END_VERTEX_ID = "entrance_exit_gate";
    private static final double DOUBLE_EPSILON = 1e-15;

    private static PathFinder pathfinder;

    /**
     * Imports the graph topology from the example file
     */
    @Before
    public void setup() {
        Graph<String, IdentifiedWeightedEdge> graph = ZooData.loadZooGraphJSON(ApplicationProvider.getApplicationContext(), GRAPH_INFO_JSON_PATH);

        pathfinder = new PathFinder(graph, START_VERTEX_ID, END_VERTEX_ID);
    }

    public void checkPathSanity(String tag, List<String> toVisit, List<GraphPath<String, IdentifiedWeightedEdge>> path, double maxWeight) {
        assertEquals(tag + ": wrong number of subpaths", toVisit.size() + 1, path.size());
        ArrayList<GraphPath<String, IdentifiedWeightedEdge>> pathArr = new ArrayList<>(path);
        HashSet<String> vertices = new HashSet<>(toVisit);
        for (int i = 0; i < pathArr.size() - 1; i++) {
            String exhibit = pathArr.get(i).getEndVertex();
            assertEquals(tag + ": path disconnected", exhibit, pathArr.get(i + 1).getStartVertex());
            assertTrue(tag + ": duplicated exhibit", vertices.contains(exhibit));
            vertices.remove(exhibit);
        }
        assertEquals(tag + ": wrong start", START_VERTEX_ID, pathArr.get(0).getStartVertex());
        assertEquals(tag + ": wrong end", END_VERTEX_ID, pathArr.get(pathArr.size() - 1).getEndVertex());
        double actualWeight = 0;
        for (GraphPath<String, IdentifiedWeightedEdge> subPath : path) {
            actualWeight += subPath.getWeight();
        }
        ;
        assertTrue(String.format("%s: weight too large (got %f, required %f)", tag, actualWeight, maxWeight), actualWeight <= maxWeight);
    }

    class SimpleTester {
        String tag;
        List<String> verticesToVisit;
        double maxWeight;

        public SimpleTester(String tag) {
            this.tag = tag;
            verticesToVisit = new ArrayList<>();
        }

        public SimpleTester addDestination(String dest) {
            verticesToVisit.add(dest);
            return this;
        }

        public SimpleTester setMaxWeight(double maxWeight) {
            this.maxWeight = maxWeight;
            return this;
        }

        public void run() {
            List<GraphPath<String, IdentifiedWeightedEdge>> calculatedPath = pathfinder.findPath(verticesToVisit);
            checkPathSanity(tag, verticesToVisit, calculatedPath, maxWeight);
        }
    }

    @Test
    public void findPath_noTargets() {
        new SimpleTester("no targets")
                .setMaxWeight(0)
                .run();
    }

    @Test
    public void findPath_oneTarget() {
        new SimpleTester("one target")
                .addDestination("gorillas")
                .setMaxWeight(1000)
                .run();
    }

    @Test
    public void findPath_twoTargets() {
        new SimpleTester("two targets")
                .addDestination("gorillas")
                .addDestination("arctic_foxes")
                .setMaxWeight(1600)
                .run();
    }

    @Test
    public void findPath_allTargets() {
        new SimpleTester("all targets")
                .addDestination("arctic_foxes")
                .addDestination("elephant_odyssey")
                .addDestination("gators")
                .addDestination("gorillas")
                .addDestination("lions")
                .setMaxWeight(2400)
                .run();
    }
}
