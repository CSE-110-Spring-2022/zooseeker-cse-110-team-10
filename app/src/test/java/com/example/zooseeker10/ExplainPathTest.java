package com.example.zooseeker10;

import static org.junit.Assert.*;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.Graph;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class ExplainPathTest {
    private static List<String> path;
    private static final double DOUBLE_EPSILON = 1E-7;

    private static Map<String, ZooData.VertexInfo> vertexInfo;
    private static Map<String, ZooData.EdgeInfo> edgeInfo;
    private static Graph<String, IdentifiedWeightedEdge> g;

    @Before
    public void setup() {
        vertexInfo = ZooData.loadVertexInfoJSON(ApplicationProvider.getApplicationContext(), ZooData.NODE_INFO_PATH);
        edgeInfo = ZooData.loadEdgeInfoJSON(ApplicationProvider.getApplicationContext(), ZooData.EDGE_INFO_PATH);
        g = ZooData.loadZooGraphJSON(ApplicationProvider.getApplicationContext(), ZooData.ZOO_GRAPH_PATH);
    }

    @Test
    public void explainPath_oneEdge() {
        path = new ArrayList<>(Arrays.asList("gorillas", "edge-1", "entrance_plaza"));
        List<DirectionsItem> explainedPath = PathFinder.explainPath(ApplicationProvider.getApplicationContext(), path);
        assertEquals("Number of explanations incorrect", 1, explainedPath.size());

        DirectionsItem directions = explainedPath.get(0);

        assertEquals("Starting location incorrect", vertexInfo.get(path.get(0)).name, directions.from);
        assertEquals("Ending location incorrect", vertexInfo.get(path.get(2)).name, directions.to);
        assertEquals("Street name incorrect", edgeInfo.get(path.get(1)).street, directions.street);
        assertEquals("Weight incorrect", g.getEdgeWeight(g.getEdge(path.get(0), path.get(2))), directions.dist, DOUBLE_EPSILON);
    }
}
