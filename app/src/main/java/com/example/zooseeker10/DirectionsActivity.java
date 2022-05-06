package com.example.zooseeker10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jgrapht.Graph;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DirectionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);
        Intent intent = getIntent();
        Gson gson = new Gson();
        Type pathIDsType = new TypeToken<List<List<String>>>() {}.getType();
        List<List<String>> paths = gson.fromJson(intent.getStringExtra("paths"), pathIDsType);

        List<String> path = paths.get(0);
        List<DirectionsItem> displayedDirections = explainPath(path);

        DirectionsListAdapter adapter = new DirectionsListAdapter();
        RecyclerView recyclerView = findViewById(R.id.directions_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setDirectionsItems(displayedDirections);

        TextView directionsTitle = findViewById(R.id.directions_title);
        Map<String, ZooData.VertexInfo> vertexInfo = ZooData.loadVertexInfoJSON(this, ZooData.NODE_INFO_PATH);
        directionsTitle.setText(String.format("Directions to %s", vertexInfo.get(path.get(path.size() - 1)).name));
    }

    public List<DirectionsItem> explainPath(List<String> path) {
        Map<String, ZooData.VertexInfo> vertexInfo = ZooData.loadVertexInfoJSON(this, ZooData.NODE_INFO_PATH);
        Map<String, ZooData.EdgeInfo> edgeInfo = ZooData.loadEdgeInfoJSON(this, ZooData.EDGE_INFO_PATH);
        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(this, ZooData.ZOO_GRAPH_PATH);
        List<DirectionsItem> explains = new ArrayList<>();
        Iterator<String> parts = path.iterator();
        String lastVertex = parts.next();
        while (parts.hasNext()) {
            String nextEdge = parts.next();
            String nextVertex = parts.next();
            DirectionsItem explain = new DirectionsItem(
                    vertexInfo.get(lastVertex).name,
                    vertexInfo.get(nextVertex).name,
                    edgeInfo.get(nextEdge).street,
                    g.getEdgeWeight(g.getEdge(lastVertex, nextVertex))
            );
            explains.add(explain);
            lastVertex = nextVertex;
        }
        return explains;
    }
}