package com.example.zooseeker10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    ArrayList<String> selectedExhibits = new ArrayList<>();
    Button planButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        planButton = this.findViewById(R.id.plan_btn);
    }

    public void onSearchButtonClicked(View view) {
        EditText searchBarView = findViewById(R.id.search_bar_view);
        String searchQuery = searchBarView.getText().toString();

        if (!searchQuery.isEmpty()) {
            Intent intent = new Intent(this, SearchResultsActivity.class);
            intent.putExtra("search_query", searchQuery);
            startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
        }

    }

    public void onPlanButtonClicked(View view) {
        Intent intent = new Intent(this, PlanActivity.class);
        intent.putStringArrayListExtra("exhibits", selectedExhibits);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String exhibitId = data.getStringExtra("exhibitId");
                selectExhibit(exhibitId);
            }
        }
    }

    public void selectExhibit(String exhibitId) {
        if (selectedExhibits.isEmpty()) {
            planButton.setVisibility(View.VISIBLE);
        }
        if (!selectedExhibits.contains(exhibitId)) {
            selectedExhibits.add(exhibitId);
            Log.d("MainActivity", exhibitId);
        }
    }

}