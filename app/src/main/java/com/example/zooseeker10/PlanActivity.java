package com.example.zooseeker10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        PlanDistListAdapter adapter = new PlanDistListAdapter();
        RecyclerView recyclerView = findViewById(R.id.plan_dist_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Graph<String, IdentifiedWeightedEdge> g;
        try {
            g = ZooData.loadZooGraphJSON(this, "sample_zoo_graph.json");
        } catch (Exception e) { return; }
        PathFinder pf = new PathFinder(g, "entrance_exit_gate", "entrance_exit_gate");
        Intent intent = getIntent();
        ArrayList<String> exhibits = intent.getStringArrayListExtra("exhibits");
        List<GraphPath<String, IdentifiedWeightedEdge>> l = pf.findPath(exhibits);
        List<PlanDistItem> items = summarizePath(l);

        adapter.setPlanDistItems(items);
    }

    /**
     * Summarize a list of paths as PlanDistItems (exhibit name and distance)
     *
     * @param paths the path to summarize
     * @return list of PlanDistItems summarizing the path
     */
    public static List<PlanDistItem> summarizePath(@NonNull List<GraphPath<String, IdentifiedWeightedEdge>> paths) {
        List<PlanDistItem> items = new ArrayList<>();
        double totalLength = 0.0;
        for (GraphPath<String, IdentifiedWeightedEdge> subPath : paths) {
            items.add(new PlanDistItem(subPath.getEndVertex(), totalLength + subPath.getWeight()));
            totalLength += subPath.getWeight();
        }
        return items;
    }

}