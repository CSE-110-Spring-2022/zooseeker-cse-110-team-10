package com.example.zooseeker10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }

    public void onSearchButtonClicked(View view) {
        EditText searchBarView = findViewById(R.id.search_bar_view);
        String searchQuery = searchBarView.getText().toString();

        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra("search_query", searchQuery);
        startActivity(intent);
    }

}