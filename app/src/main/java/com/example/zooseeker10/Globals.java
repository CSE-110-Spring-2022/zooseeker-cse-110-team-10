package com.example.zooseeker10;

import android.content.Context;

public class Globals {
    public static class ZooData {
        public static final String ZOO_GRAPH_PATH = "zoo_graph.json";
        public static final String NODE_INFO_PATH = "exhibit_info.json";
        public static final String EDGE_INFO_PATH = "trail_info.json";
        public static final String ENTRANCE_GATE_ID = "entrance_exit_gate";
        public static final String EXIT_GATE_ID = "entrance_exit_gate";
    }

    public static class ZooDataTest {
        public static final String EDGE_INFO_PATH = "sample_edge_info.json";
        public static final String NODE_INFO_PATH = "sample_node_info.json";
        public static final String ZOO_GRAPH_PATH = "sample_zoo_graph.json";
        public static final String ENTRANCE_GATE_ID = "entrance_exit_gate";
        public static final String EXIT_GATE_ID = "entrance_exit_gate";

        public static void setLegacy(Context context) {
            com.example.zooseeker10.ZooData.loadVertexInfoJSON(context, NODE_INFO_PATH);
            com.example.zooseeker10.ZooData.loadEdgeInfoJSON(context, EDGE_INFO_PATH);
            com.example.zooseeker10.ZooData.loadZooGraphJSON(context, ZOO_GRAPH_PATH);
        }
    }

}