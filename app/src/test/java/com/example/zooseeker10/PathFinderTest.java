package com.example.zooseeker10;

import org.junit.*;
import static org.junit.Assert.*;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
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

    @Test
    public void findPath_noTargets() {
        verticesToVisit = new ArrayList<>();
        List<GraphPath<String, IdentifiedWeightedEdge>> calculatedPath = pathfinder.findPath(verticesToVisit);

        assertEquals("Number of subpaths incorrect", 1, calculatedPath.size());

        GraphPath<String, IdentifiedWeightedEdge> path = calculatedPath.get(0);
        assertEquals("Weight of subpath incorrect", 0, path.getWeight(), DOUBLE_EPSILON);
        assertEquals("Number of vertices along subpath incorrect", 0, path.getLength());
        assertEquals("Number of vertices in subpath incorrect", 1, path.getVertexList().size());
        assertEquals("Vertex in subpath incorrect", START_VERTEX_ID, path.getStartVertex());
    }
}
