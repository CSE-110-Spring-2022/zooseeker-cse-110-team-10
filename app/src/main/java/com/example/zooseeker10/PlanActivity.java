package com.example.zooseeker10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class PlanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
        Graph<String, IdentifiedWeightedEdge> g;
        try {
            g = ZooData.loadZooGraphJSON(new InputStreamReader(this.getAssets().open("sample_zoo_graph.json")));
        } catch (Exception e) { return; }
        PathFinder pf = new PathFinder(g, "entrance_exit_gate", "entrance_exit_gate");
        String[] exhibits = { "gorillas", "arctic_foxes" };
        List<GraphPath<String, IdentifiedWeightedEdge>> l = pf.findPath(Arrays.asList(exhibits));
        for (GraphPath<String, IdentifiedWeightedEdge> subPath : l) {
            Log.d("PlanActivity", "-----------");
            Log.d("PlanActivity", String.format("distance is %f", subPath.getWeight()));
            Iterator<String> v = subPath.getVertexList().iterator();
            Iterator<IdentifiedWeightedEdge> e = subPath.getEdgeList().iterator();
            Log.d("PlanActivity", "[1] vertex: " + v.next());
            int i = 2;
            while (e.hasNext()) {
                Log.d("PlanActivity", String.format("[%d] edge: %s", i++, e.next().getId()));
                Log.d("PlanActivity", String.format("[%d] vertex: %s", i++, v.next()));
            }
        }
    }
}