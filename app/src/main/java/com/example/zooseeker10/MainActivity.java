package com.example.zooseeker10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class MainActivity extends AppCompatActivity {
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    private final PermissionChecker permissionChecker = new PermissionChecker(this);

    public ArrayList<String> selectedExhibitIds = new ArrayList<>();
    private RecyclerView recyclerView;
    private Button planButton;
    private EditText searchBarView;
    private TextView exhibitsCountView;
    private SelectedExhibitsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Permissions Setup */
        if (permissionChecker.ensurePermissions()) return;

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

    private boolean ensurePermissions() {
        return permissionChecker.ensurePermissions();
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
        intent.putStringArrayListExtra("exhibits", selectedExhibitIds);
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
                selectExhibit(exhibitId);
                updateAdapter();
            }
        }
    }

    public void selectExhibit(String exhibitId) {
        if (selectedExhibitIds.isEmpty()) {
            planButton.setVisibility(View.VISIBLE);
        }
        if (!selectedExhibitIds.contains(exhibitId)) {
            selectedExhibitIds.add(0, exhibitId);
            Log.d("MainActivity", exhibitId);
        }
    }

    public void updateAdapter() {
            Map<String, ZooData.VertexInfo> exhibits = ZooData.getVertexInfo(this);
            List<ZooData.VertexInfo> selectedExhibits = selectedExhibitIds.stream()
                    .map(exhibits::get)
                    .collect(Collectors.toList());


            adapter.setSelectedExhibits(selectedExhibits);

            exhibitsCountView.setText("(" + selectedExhibitIds.size() + ")");
            Log.d("SelectedExhibitIds", selectedExhibitIds.toString());
    }
}