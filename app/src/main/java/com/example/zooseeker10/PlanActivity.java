package com.example.zooseeker10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlanActivity extends AppCompatActivity {
    ZooPlan plan;
    PlanDistListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        adapter = new PlanDistListAdapter();
        RecyclerView recyclerView = findViewById(R.id.plan_dist_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Graph<String, IdentifiedWeightedEdge> g = ZooData.getZooGraph(this);
        Map<String, ZooData.VertexInfo> vertexInfos = ZooData.getVertexInfo(this);
        PathFinder pf = new PathFinder(g, Globals.ZooData.ENTRANCE_GATE_ID, Globals.ZooData.EXIT_GATE_ID);
        Intent intent = getIntent();
        ArrayList<String> exhibits = intent.getStringArrayListExtra("exhibits");

        List<String> vertexIDs = new ArrayList<>();
        for (String exhibit : exhibits) {
            ZooData.VertexInfo vertexInfo = vertexInfos.get(exhibit);
            String vertexID = vertexInfo.hasGroup() ? vertexInfo.groupId : exhibit;
            if (!vertexIDs.contains(vertexID)) {
                vertexIDs.add(vertexID);
            }
        }
        plan = pf.findPath(vertexIDs);
        List<PlanDistItem> items = plan.summarizePath(this);
        adapter.setPlanDistItems(items);
    }

    public void onDirectionsBtnClicked(View view) {
        Intent intent = new Intent(this, DirectionsActivity.class);
        Log.d("PlanActivity", "TODO");
        intent.putExtra("paths", plan);
        startActivity(intent);
    }

    public void onRestartPlanButtonClicked(View view) {
        adapter.clear(); // TODO: Clear the plan and then update the adapter.
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}