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

        ZooDataDao zooDataDao = ZooDatabase.getSingleton(this).zooDataDao();
        List<ZooData.VertexInfo> searchResults = zooDataDao
                .getQuerySearch(ZooData.VertexInfo.Kind.EXHIBIT, '%' + searchQuery + '%');
        SearchResultsAdapter adapter = new SearchResultsAdapter();
        adapter.setSearchResults(searchResults);
        adapter.setOnAddBtnClickedHandler(this::onAddExhibitClicked);

        recyclerView = findViewById(R.id.search_results);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void onAddExhibitClicked(ZooData.VertexInfo exhibit) {
        String exhibitId = exhibit.id;
        Intent intent = getIntent();
        Log.d("SearchResultsActivity", "new exhibit: " + exhibitId);
        intent.putExtra("exhibitId", exhibitId);
        setResult(RESULT_OK, intent);
        this.finish();
    }
}