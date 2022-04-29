package com.example.zooseeker10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.io.InputStreamReader;

public class PlanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        Graph<String, IdentifiedWeightedEdge> g;
        try {
            g = ZooData.loadZooGraphJSON(new InputStreamReader(this.getAssets().open("sample_zoo_graph.json")));
        } catch (Exception e) { return; }
        DijkstraShortestPath gD = new DijkstraShortestPath(g);
        ShortestPathAlgorithm.SingleSourcePaths<String, IdentifiedWeightedEdge> pathGenerator = gD.getPaths("entrance_exit_gate");
        GraphPath<String, IdentifiedWeightedEdge> l = pathGenerator.getPath("gorillas");
        for (String v : l.getVertexList()) {
            Log.d("PlanActivity", "vertex %d: " + v);
        }
        for (IdentifiedWeightedEdge e: l.getEdgeList()) {
            Log.d("PlanActivity", "edge %d: " + e.getId());
        }
    }
}