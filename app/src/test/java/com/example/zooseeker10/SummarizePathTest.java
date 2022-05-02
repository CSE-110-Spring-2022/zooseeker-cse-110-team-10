package com.example.zooseeker10;

import static org.junit.Assert.*;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphWalk;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SummarizePathTest {
    private static Graph<String, IdentifiedWeightedEdge> graph;

    /**
     * Imports the graph topology from the example file
     */
    @Before
    public void setup() {
        graph = ZooData.loadZooGraphJSON(ApplicationProvider.getApplicationContext(), ZooData.ZOO_GRAPH_PATH);
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
            List<PlanDistItem> actualItems = PlanActivity.summarizePath(ApplicationProvider.getApplicationContext(), paths);
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
                .expectItem("Entrance and Exit Gate", 0.0)
                .run();
    }

    @Test
    public void testOneExhibit() {
        new SimpleTester("one exhibit")
                .planVertex("entrance_exit_gate")
                .planVertex("entrance_plaza")
                .planVertex("gorillas")
                .planPath(210.0)
                .expectItem("Gorillas", 210.0)
                .planVertex("gorillas")
                .planVertex("entrance_plaza")
                .planVertex("entrance_exit_gate")
                .planPath(210.0)
                .expectItem("Entrance and Exit Gate", 420.0)
                .run();
    }

    @Test
    public void testManyExhibits() {
        new SimpleTester("many exhibits")
                .planVertex("entrance_exit_gate")
                .planVertex("entrance_plaza")
                .planVertex("gorillas")
                .planPath(210.0)
                .expectItem("Gorillas", 210.0)
                .planVertex("gorillas")
                .planVertex("lions")
                .planPath(200.0)
                .expectItem("Lions", 410.0)
                .planVertex("lions")
                .planVertex("gators")
                .planPath(200.0)
                .expectItem("Alligators", 610.0)
                .planVertex("gators")
                .planVertex("entrance_plaza")
                .planVertex("entrance_exit_gate")
                .planPath(110.0)
                .expectItem("Entrance and Exit Gate", 720.0)
                .run();
    }
}
