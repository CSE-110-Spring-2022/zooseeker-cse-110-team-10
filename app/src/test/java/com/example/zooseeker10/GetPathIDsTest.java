package com.example.zooseeker10;

import static org.junit.Assert.assertEquals;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphWalk;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class GetPathIDsTest {
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
        List<List<String>> expect;
        List<String> expectWksp;

        public SimpleTester(String tag) {
            this.tag = tag;
            paths = new ArrayList<>();
            pathWksp = new ArrayList<>();
            expect = new ArrayList<>();
            expectWksp = new ArrayList<>();
        }

        public SimpleTester planVertex(String v) {
            pathWksp.add(v);
            return this;
        }

        public SimpleTester expectID(String id) {
            expectWksp.add(id);
            return this;
        }

        public SimpleTester finishPath() {
            paths.add(new GraphWalk<>(graph, pathWksp, 0.0));
            pathWksp = new ArrayList<>();
            expect.add(expectWksp);
            expectWksp = new ArrayList<>();
            return this;
        }

        public void run() {
            List<List<String>> actualIDs = PlanActivity.getPathIDs(paths);
            assertEquals(tag, expect, actualIDs);
        }
    }

    @Test
    public void testNoExhibts() {
        new SimpleTester("zero exhibit")
                .planVertex("entrance_exit_gate")
                .expectID("entrance_exit_gate")
                .finishPath()
                .run();
    }

    @Test
    public void testOneExhibit() {
        new SimpleTester("one exhibit")
                .planVertex("entrance_exit_gate")
                .planVertex("entrance_plaza")
                .planVertex("gorillas")
                .expectID("entrance_exit_gate")
                .expectID("edge-0")
                .expectID("entrance_plaza")
                .expectID("edge-1")
                .expectID("gorillas")
                .finishPath()
                .planVertex("gorillas")
                .planVertex("entrance_plaza")
                .planVertex("entrance_exit_gate")
                .expectID("gorillas")
                .expectID("edge-1")
                .expectID("entrance_plaza")
                .expectID("edge-0")
                .expectID("entrance_exit_gate")
                .finishPath()
                .run();
    }

    @Test
    public void testManyExhibits() {
        new SimpleTester("many exhibits")
                .planVertex("entrance_exit_gate")
                .planVertex("entrance_plaza")
                .planVertex("gorillas")
                .expectID("entrance_exit_gate")
                .expectID("edge-0")
                .expectID("entrance_plaza")
                .expectID("edge-1")
                .expectID("gorillas")
                .finishPath()
                .planVertex("gorillas")
                .planVertex("lions")
                .expectID("gorillas")
                .expectID("edge-2")
                .expectID("lions")
                .finishPath()
                .planVertex("lions")
                .planVertex("gators")
                .expectID("lions")
                .expectID("edge-6")
                .expectID("gators")
                .finishPath()
                .planVertex("gators")
                .planVertex("entrance_plaza")
                .planVertex("entrance_exit_gate")
                .expectID("gators")
                .expectID("edge-5")
                .expectID("entrance_plaza")
                .expectID("edge-0")
                .expectID("entrance_exit_gate")
                .finishPath()
                .run();
    }
}
