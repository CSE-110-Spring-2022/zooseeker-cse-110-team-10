package com.example.zooseeker10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    private final String RESULT_TITLE_FORMAT = "Results with \"%s\".";
    private  SearchResults searchResults;
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        Bundle extras = getIntent().getExtras();
        if (extras.containsKey("dummy")) {
            ZooDatabase.getSingleton(this);
            this.finish();
        }
        String searchQuery = extras.getString("search_query");
        
        TextView searchQueryView = findViewById(R.id.search_query_view);
        searchQueryView.setText(String.format(RESULT_TITLE_FORMAT, searchQuery));

        this.searchResults = new SearchResults(this, searchQuery);

        SearchResultsAdapter adapter = new SearchResultsAdapter();
        adapter.setSearchResults(this.searchResults.getSearchResults());
        adapter.setOnAddBtnClickedHandler(this::onAddExhibitClicked);

        recyclerView = findViewById(R.id.search_results);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void onAddExhibitClicked(ZooData.VertexInfo exhibit) {
        String exhibitId = exhibit.id;
        Intent intent = getIntent();
        Log.d("SearchResultsActivity", "new exhibit: " + exhibitId);
        /**
         * Citation:
         * https://stackoverflow.com/questions/920306/sending-data-back-to-the-main-activity-in-android
         * Sending the data back to main activity in android
         * May 8th, 2022
         * Used mainly for information on getting a result back from search activity but used a method startActivityForResult as an outline
         * D.J
         */
        intent.putExtra("exhibitId", exhibitId);
        setResult(RESULT_OK, intent);
        this.finish();
    }
}