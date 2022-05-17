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

public class PlanActivity extends AppCompatActivity {

    ZooPlan plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        PlanDistListAdapter adapter = new PlanDistListAdapter();
        RecyclerView recyclerView = findViewById(R.id.plan_dist_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Graph<String, IdentifiedWeightedEdge> g;
        g = ZooData.getZooGraph(this);
        PathFinder pf = new PathFinder(g, ZooData.ENTRANCE_GATE_ID, ZooData.EXIT_GATE_ID);
        Intent intent = getIntent();
        ArrayList<String> exhibits = intent.getStringArrayListExtra("exhibits");
        plan = pf.findPath(exhibits);
        List<PlanDistItem> items = plan.summarizePath(this);
        adapter.setPlanDistItems(items);

    }

    public void onDirectionsBtnClicked(View view) {
        Intent intent = new Intent(this, DirectionsActivity.class);
        Log.d("PlanActivity", "TODO");
        intent.putExtra("paths", plan);
        startActivity(intent);
    }
}