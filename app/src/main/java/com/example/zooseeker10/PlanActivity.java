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

        Intent intent = getIntent();
        if (intent.hasExtra(Globals.MapKeys.ZOOPLAN)) {
            plan = (ZooPlan) intent.getSerializableExtra(Globals.MapKeys.ZOOPLAN);
        }
        else if (intent.hasExtra("exhibits")) {
            ArrayList<String> exhibits = intent.getStringArrayListExtra("exhibits");
            plan = generatePlan(exhibits);
        }
        else {
            throw new IllegalStateException("PlanActivity unknown state");
        }
        ArrayList<String> exhibits = intent.getStringArrayListExtra("exhibits");

        List<PlanDistItem> items = plan.summarizePath(this);
        adapter.setPlanDistItems(items);
    }

    private ZooPlan generatePlan(ArrayList<String> exhibits) {
        Graph<String, IdentifiedWeightedEdge> g = ZooData.getZooGraph(this);
        Map<String, ZooData.VertexInfo> vertexInfos = ZooData.getVertexInfo(this);
        PathFinder pf = new PathFinder(g, Globals.ZooData.ENTRANCE_GATE_ID, Globals.ZooData.EXIT_GATE_ID);

        List<String> vertexIDs = new ArrayList<>();
        for (String exhibit : exhibits) {
            ZooData.VertexInfo vertexInfo = vertexInfos.get(exhibit);
            String vertexID = vertexInfo.hasGroup() ? vertexInfo.groupId : exhibit;
            if (!vertexIDs.contains(vertexID)) {
                vertexIDs.add(vertexID);
            }
        }
        return pf.findPath(vertexIDs);
    }

    @Override
    protected void onStop() {
        super.onStop();

        StateManager.storePlanState(plan);
    }

    public void onDirectionsBtnClicked(View view) {
        Intent intent = new Intent(this, DirectionsActivity.class);
        Log.d("PlanActivity", "TODO");
        intent.putExtra(Globals.MapKeys.ZOOPLAN, plan);
        startActivity(intent);
    }

    public void onRestartPlanButtonClicked(View view) {
        StateManager.storeMainState(new SelectedExhibits(Globals.State.activity));
        adapter.clear(); // TODO: Clear the plan and then update the adapter.
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}