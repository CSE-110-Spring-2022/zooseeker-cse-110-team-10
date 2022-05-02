package com.example.zooseeker10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    ArrayList<String> selectedExhibits = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        Map<String, ZooData.VertexInfo> vertexInfoMap = ZooData.loadVertexInfoJSON(this, "sample_node_info.json");
//
//        for (ZooData.VertexInfo vertexInfo: vertexInfoMap.values()) {
//            Log.d("MainActivity", vertexInfo.toString());
//        }

    }

    int i = 0; // TODO:this is hack. obviously
    public void onSearchButtonClicked(View view) {
        i++;
        if (i == 4) {
            Intent intent = new Intent(this, PlanActivity.class);
            intent.putStringArrayListExtra("exhibits", selectedExhibits);
            startActivity(intent);
        }
        EditText searchBarView = findViewById(R.id.search_bar_view);
        String searchQuery = searchBarView.getText().toString();

        if (!searchQuery.isEmpty()) {
            Intent intent = new Intent(this, SearchResultsActivity.class);
            intent.putExtra("search_query", searchQuery);
            startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String exhibitId = data.getStringExtra("exhibitId");
                selectedExhibits.add(exhibitId);
                Log.d("MainActivity", exhibitId);
            }
        }
    }

}