package com.example.zooseeker10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PlanActivity extends AppCompatActivity {

    List<GraphPath<String, IdentifiedWeightedEdge>> paths;

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
        paths = pf.findPath(exhibits);
        List<PlanDistItem> items = summarizePath(this, paths);
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
            Map<String, ZooData.VertexInfo> map = ZooData.getVertexInfo(context);
            String exhibitName = map.get(endVertexId).name;
            items.add(new PlanDistItem(exhibitName, totalLength + subPath.getWeight()));
            totalLength += subPath.getWeight();
        }

        return items;
    }

    /**
     * Converts each path into lists of vertex and edge IDs it goes through
     *
     * @param paths the path to convert to strings
     * @return for each path in the list, a list of the vertex and edge IDs it goes through
     */
    public static List<List<String>> getPathIDs(@NonNull List<GraphPath<String, IdentifiedWeightedEdge>> paths) {
        List<List<String>> pathIDs = new ArrayList<>();
        for (GraphPath<String, IdentifiedWeightedEdge> path : paths) {
            List<String> thisPathIDs = new ArrayList<>();
            Iterator<String> vertexIt = path.getVertexList().iterator();
            Iterator<IdentifiedWeightedEdge> edgeIt = path.getEdgeList().iterator();
            thisPathIDs.add(vertexIt.next());
            while (edgeIt.hasNext()) {
                thisPathIDs.add(edgeIt.next().getId());
                thisPathIDs.add(vertexIt.next());
            }
            pathIDs.add(thisPathIDs);
        }
        return pathIDs;
    }

    public void onDirectionsBtnClicked(View view) {
        Intent intent = new Intent(this, DirectionsActivity.class);
        List<List<String>> pathIDs = getPathIDs(paths);
        Gson gson = new Gson();
        Type pathIDsType = new TypeToken<List<List<String>>>() {}.getType();
        String pathsSerialized = gson.toJson(pathIDs, pathIDsType);
        Log.d("PlanActivity", pathsSerialized);
        intent.putExtra("paths", pathsSerialized);
        startActivity(intent);
    }
}