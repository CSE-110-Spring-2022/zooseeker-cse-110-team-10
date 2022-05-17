package com.example.zooseeker10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class DirectionsActivity extends AppCompatActivity {

    ZooPlan plan;
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
        plan = (ZooPlan)intent.getSerializableExtra("paths");

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
        if (false) {
            nextButton.setVisibility(View.INVISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
        }
        List<DirectionsItem> displayedDirections = plan.explainPath(this, newPage);
        dLAdapter.setDirectionsItems(displayedDirections);
        directionsTitle.setText(String.format("Directions from %s to %s",
                "todo", "todo"));
        currentPage = newPage;
    }
}