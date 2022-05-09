package com.example.zooseeker10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    List<List<String>> paths;
    Button previousButton;
    Button nextButton;
    TextView directionsTitle;
    DirectionsListAdapter dLAdapter;
    Map<String, ZooData.VertexInfo> vertexInfo;
    int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);
        Intent intent = getIntent();
        Gson gson = new Gson();
        Type pathIDsType = new TypeToken<List<List<String>>>() {}.getType();
        paths = gson.fromJson(intent.getStringExtra("paths"), pathIDsType);

        vertexInfo = ZooData.getVertexInfo(this);

        previousButton = findViewById(R.id.directions_previous_button);
        nextButton = findViewById(R.id.directions_next_button);

        // Setup for views with text
        dLAdapter = new DirectionsListAdapter();
        RecyclerView recyclerView = findViewById(R.id.directions_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dLAdapter);
        directionsTitle = findViewById(R.id.directions_title);

        // Loads up initial page
        setDirectionsPage(0);

        previousButton.setOnClickListener(
                view -> { setDirectionsPage(currentPage - 1); }
        );

        nextButton.setOnClickListener(
                view -> { setDirectionsPage(currentPage + 1); }
        );

    }

    public void setDirectionsPage(int newPage) {

        if (newPage == 0) {
            previousButton.setVisibility(View.INVISIBLE);
        } else {
            previousButton.setVisibility(View.VISIBLE);
        }
        if (newPage == paths.size() - 1) {
            nextButton.setVisibility(View.INVISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
        }
        List<String> path = paths.get(newPage);
        List<DirectionsItem> displayedDirections = PathFinder.explainPath(this, path);
        dLAdapter.setDirectionsItems(displayedDirections);
        directionsTitle.setText(String.format("Directions from %s\nto %s",
                vertexInfo.get(path.get(0)).name,
                vertexInfo.get(path.get(path.size() - 1)).name));
        currentPage = newPage;
    }
}