package com.example.zooseeker10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
            g = ZooData.loadZooGraphJSON(this, ZooData.ZOO_GRAPH_PATH);
        } catch (Exception e) { return; }
        PathFinder pf = new PathFinder(g, ZooData.ENTRANCE_GATE_ID, ZooData.EXIT_GATE_ID);
        Intent intent = getIntent();
        ArrayList<String> exhibits = intent.getStringArrayListExtra("exhibits");
        List<GraphPath<String, IdentifiedWeightedEdge>> l = pf.findPath(exhibits);
        List<PlanDistItem> items = summarizePath(this, l);

        adapter.setPlanDistItems(items);
    }

    /**
     * Summarize a list of paths as PlanDistItems (exhibit name and distance)
     *
     * @param paths the path to summarize
     * @return list of PlanDistItems summarizing the path
     */
    public static List<PlanDistItem> summarizePath(Context context, @NonNull List<GraphPath<String, IdentifiedWeightedEdge>> paths) {
        List<PlanDistItem> items = new ArrayList<>();
        double totalLength = 0.0;
        for (GraphPath<String, IdentifiedWeightedEdge> subPath : paths) {
            String endVertexId = subPath.getEndVertex();
            Map<String, ZooData.VertexInfo> map = ZooData.loadVertexInfoJSON(context, ZooData.NODE_INFO_PATH);
            String exhibitName = map.get(endVertexId).name;
            items.add(new PlanDistItem(exhibitName, totalLength + subPath.getWeight()));
            totalLength += subPath.getWeight();
        }
        return items;
    }

}