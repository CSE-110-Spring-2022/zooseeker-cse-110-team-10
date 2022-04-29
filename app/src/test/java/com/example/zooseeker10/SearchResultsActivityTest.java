package com.example.zooseeker10;

import static org.junit.Assert.assertEquals;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SearchResultsActivityTest {
//    @Rule
//    public ActivityScenarioRule<MainActivity> rule =new ActivityScenarioRule<>(MainActivity.class);
//
//    @Test
//    public void testPassedSearchQuery() {
//        ActivityScenario<MainActivity> scenario = rule.getScenario();
//
//        scenario.moveToState(Lifecycle.State.CREATED);
//
//        scenario.onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
//            @Override
//            public void perform(MainActivity activity) {
//                String searchQuery = "Test search query";
//                EditText searchBarView = activity.findViewById(R.id.search_bar_view);
//                Button searchButton = activity.findViewById(R.id.search_btn);
//
//                searchBarView.setText(searchQuery);
//                searchButton.performClick();
//
//                TextView searchQueryView = activity.findViewById(R.id.search_query_view);
//                assertEquals("Search results with \"" + searchQuery + "\".", searchQueryView.getText().toString());
//
//            }
//        });
//    }
}
