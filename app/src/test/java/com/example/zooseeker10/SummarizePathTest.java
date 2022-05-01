package com.example.zooseeker10;

import static org.junit.Assert.*;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphWalk;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class SummarizePathTest {
    private static final String GRAPH_INFO_JSON_PATH = "sample_zoo_graph.json";

    private static Graph<String, IdentifiedWeightedEdge> graph;

    /**
     * Imports the graph topology from the example file
     */
    @Before
    public void setup() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(GRAPH_INFO_JSON_PATH);
        Reader reader = new InputStreamReader(inputStream);
        graph = ZooData.loadZooGraphJSON(reader);
    }

    class SimpleTester {
        String tag;
        List<GraphPath<String, IdentifiedWeightedEdge>> paths;
        List<String> pathWksp;
        List<PlanDistItem> items;

        public SimpleTester(String tag) {
            this.tag = tag;
            paths = new ArrayList<>();
            items = new ArrayList<>();
            pathWksp = new ArrayList<>();
        }

        public SimpleTester planVertex(String v) {
            pathWksp.add(v);
            return this;
        }

        public SimpleTester planPath(double dist) {
            paths.add(new GraphWalk<String, IdentifiedWeightedEdge>(graph, pathWksp, dist));
            pathWksp.clear();
            return this;
        }

        public SimpleTester expectItem(String expectExhibit, double expectDistance) {
            items.add(new PlanDistItem(expectExhibit, expectDistance));
            return this;
        }

        public void run() {
            List<PlanDistItem> actualItems = PlanActivity.summarizePath(paths);
            assertEquals(tag + " wrong number of items", items.size(), actualItems.size());
            for (int i = 0; i < items.size(); i++) {
                assertEquals(tag + " wrong exhibit", items.get(i).exhibitName, actualItems.get(i).exhibitName);
                assertEquals(tag + " wrong distance", items.get(i).distance, actualItems.get(i).distance, 1e-7);
            }
        }
    }

    @Test
    public void testNoExhibts() {
        new SimpleTester("zero exhibit")
                .planVertex("entrance_exit_gate")
                .planPath(0.0)
                .expectItem("entrance_exit_gate", 0.0)
                .run();
    }

    @Test
    public void testOneExhibit() {
        new SimpleTester("one exhibit")
                .planVertex("entrance_exit_gate")
                .planVertex("entrance_plaza")
                .planVertex("gorillas")
                .planPath(210.0)
                .expectItem("gorillas", 210.0)
                .planVertex("gorillas")
                .planVertex("entrance_plaza")
                .planVertex("entrance_exit_gate")
                .planPath(210.0)
                .expectItem("entrance_exit_gate", 420.0)
                .run();
    }

    @Test
    public void testManyExhibits() {
        new SimpleTester("many exhibits")
                .planVertex("entrance_exit_gate")
                .planVertex("entrance_plaza")
                .planVertex("gorillas")
                .planPath(210.0)
                .expectItem("gorillas", 210.0)
                .planVertex("gorillas")
                .planVertex("lions")
                .planPath(200.0)
                .expectItem("lions", 410.0)
                .planVertex("lions")
                .planVertex("gators")
                .planPath(200.0)
                .expectItem("gators", 610.0)
                .planVertex("gators")
                .planVertex("entrance_plaza")
                .planVertex("entrance_exit_gate")
                .planPath(110.0)
                .expectItem("entrance_exit_gate", 720.0)
                .run();
    }
}
