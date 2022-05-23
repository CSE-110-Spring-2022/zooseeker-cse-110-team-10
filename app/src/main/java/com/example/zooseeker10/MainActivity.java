package com.example.zooseeker10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class MainActivity extends AppCompatActivity {
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    public final SelectedExhibits selectedExhibits = new SelectedExhibits(this);

    private RecyclerView recyclerView;
    public Button planButton;
    private EditText searchBarView;
    private TextView exhibitsCountView;
    private SelectedExhibitsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        planButton = findViewById(R.id.plan_btn);
        recyclerView = findViewById(R.id.selected_exhibits);
        searchBarView = findViewById(R.id.search_bar_view);
        exhibitsCountView = findViewById(R.id.exhibits_count_view);

        adapter = new SelectedExhibitsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra("dummy", true);
        startActivity(intent);
    }

    public void onSearchButtonClicked(View view) {
        String searchQuery = searchBarView.getText().toString();

        if (!searchQuery.isEmpty()) {
            Intent intent = new Intent(this, SearchResultsActivity.class);
            intent.putExtra("search_query", searchQuery);
            /**
             * Citation:
             * https://stackoverflow.com/questions/920306/sending-data-back-to-the-main-activity-in-android
             * Sending the data back to main activity in android
             * May 8th, 2022
             * Used mainly for information on getting a result back from search activity but used a method startActivityForResult as an outline
             * D.J
             */
            startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
        }
    }

    public void onPlanButtonClicked(View view) {
        Intent intent = new Intent(this, PlanActivity.class);
        intent.putStringArrayListExtra("exhibits", selectedExhibits.selectedExhibitIds);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * Citation:
         * https://stackoverflow.com/questions/920306/sending-data-back-to-the-main-activity-in-android
         * Sending the data back to main activity in android
         * May 8th, 2022
         * Used mainly for information on getting a result back from search activity but used a method startActivityForResult as an outline
         * D.J
         */
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String exhibitId = data.getStringExtra("exhibitId");
                selectedExhibits.addExhibit(exhibitId);
                updateAdapter();
            }
        }
    }

    public void updateAdapter() {
            //List<ZooData.VertexInfo> selectedExhibits=SelectedExhibits.getExhibits();
            Map<String, ZooData.VertexInfo> exhibits = ZooData.getVertexInfo(this);
            List<ZooData.VertexInfo> selectedExhibits = this.selectedExhibits.selectedExhibitIds.stream()
                    .map(exhibits::get)
                    .collect(Collectors.toList());

            adapter.setSelectedExhibits(selectedExhibits);

            exhibitsCountView.setText("(" + this.selectedExhibits.selectedExhibitIds.size() + ")");
            Log.d("SelectedExhibitIds", this.selectedExhibits.selectedExhibitIds.toString());
    }
}