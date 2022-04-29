package com.example.zooseeker10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchResultsActivity extends AppCompatActivity {

    private String searchQuery;
    private final String RESULT_TITLE_FORMAT = "Results with \"%s\".";
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        Bundle extras = getIntent().getExtras();
        this.searchQuery = extras.getString("search_query");


        TextView searchQueryView = findViewById(R.id.search_query_view);
        searchQueryView.setText(String.format(RESULT_TITLE_FORMAT, this.searchQuery));

        SearchResultsAdapter adapter = new SearchResultsAdapter();
//        adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.search_results);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Map<String, ZooData.VertexInfo> map = ZooData.loadVertexInfoJSON(this, "sample_node_info.json");
        List<ZooData.VertexInfo> searchResults = new ArrayList<>(map.values());
        adapter.setSearchResults(searchResults);
    }
}