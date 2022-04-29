package com.example.zooseeker10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SearchResultsActivity extends AppCompatActivity {

    private String searchQuery;
    private final String RESULT_TITLE_FORMAT = "Results with \"%s\".";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        Bundle extras = getIntent().getExtras();
        this.searchQuery = extras.getString("search_query");


        TextView searchQueryView = findViewById(R.id.search_query_view);
        searchQueryView.setText(String.format(RESULT_TITLE_FORMAT, this.searchQuery));
    }
}