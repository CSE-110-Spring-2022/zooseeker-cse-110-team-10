package com.example.zooseeker10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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

        // Really scuffed
        final int[] indexArr = {0};
        final Context[] contextArr = {this};

        Map<String, ZooData.VertexInfo> vertexInfo = ZooData.loadVertexInfoJSON(this, ZooData.NODE_INFO_PATH);

        Button previousButton = findViewById(R.id.directions_previousButton);
        Button nextButton = findViewById(R.id.directions_nextButton);

        // Setup for views with text
        DirectionsListAdapter adapter = new DirectionsListAdapter();
        RecyclerView recyclerView = findViewById(R.id.directions_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        TextView directionsTitle = findViewById(R.id.directions_title);

        // Loads up initial page
        List<String> initialPath = paths.get(0);
        adapter.setDirectionsItems(PathFinder.explainPath(this, initialPath));
        directionsTitle.setText(String.format("Directions from %s\nto %s",
                                vertexInfo.get(initialPath.get(0)).name,
                                vertexInfo.get(initialPath.get(initialPath.size() - 1)).name));

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentIndex = --indexArr[0];

                nextButton.setVisibility(View.VISIBLE);
                nextButton.setClickable(true);
                if (currentIndex == 0) {
                    previousButton.setVisibility(View.INVISIBLE);
                    previousButton.setClickable(false);
                }

                List<String> path = paths.get(currentIndex);
                List<DirectionsItem> displayedDirections = PathFinder.explainPath(contextArr[0], path);
                adapter.setDirectionsItems(displayedDirections);
                directionsTitle.setText(String.format("Directions from %s\nto %s",
                                        vertexInfo.get(path.get(0)).name,
                                        vertexInfo.get(path.get(path.size() - 1)).name));
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentIndex = ++indexArr[0];

                previousButton.setVisibility(View.VISIBLE);
                previousButton.setClickable(true);
                if (currentIndex >= paths.size() - 1) {
                    nextButton.setVisibility(View.INVISIBLE);
                    nextButton.setClickable(false);
                }

                List<String> path = paths.get(currentIndex);
                List<DirectionsItem> displayedDirections = PathFinder.explainPath(contextArr[0], path);
                adapter.setDirectionsItems(displayedDirections);
                directionsTitle.setText(String.format("Directions from %s\nto %s",
                                        vertexInfo.get(path.get(0)).name,
                                        vertexInfo.get(path.get(path.size() - 1)).name));
            }
        });
    }
}