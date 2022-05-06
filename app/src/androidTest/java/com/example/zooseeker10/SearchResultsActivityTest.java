package com.example.zooseeker10;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Intent;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SearchResultsActivityTest {

    @Test
    public void testValidNameQuery() {
        String searchQuery = "goril";
        Intent intent
                = new Intent(ApplicationProvider.getApplicationContext(), SearchResultsActivity.class);
        intent.putExtra("search_query", searchQuery);
        ActivityScenario<SearchResultsActivity> scenario = ActivityScenario.launch(intent);

        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            TextView searchQueryView = activity.findViewById(R.id.search_query_view);
            assertEquals("Results with \"" + searchQuery + "\".", searchQueryView.getText().toString());

            RecyclerView recyclerView = activity.recyclerView;

            RecyclerView.ViewHolder vh1 = recyclerView.findViewHolderForAdapterPosition(0);
            RecyclerView.ViewHolder vh2 = recyclerView.findViewHolderForAdapterPosition(1);

            assertNotNull(vh1);
            assertNull(vh2);

            String expectedId = "gorillas";
            String id = ((SearchResultsAdapter.ViewHolder) vh1).getSearchResult().id;
            assertEquals(expectedId, id);

        });
    }

    @Test
    public void testValidTagQuery() {
        String searchQuery = "mm";
        Intent intent
                = new Intent(ApplicationProvider.getApplicationContext(), SearchResultsActivity.class);
        intent.putExtra("search_query", searchQuery);
        ActivityScenario<SearchResultsActivity> scenario = ActivityScenario.launch(intent);

        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            TextView searchQueryView = activity.findViewById(R.id.search_query_view);
            assertEquals("Results with \"" + searchQuery + "\".", searchQueryView.getText().toString());

            RecyclerView recyclerView = activity.recyclerView;

            RecyclerView.ViewHolder vh1 = recyclerView.findViewHolderForAdapterPosition(0);
            RecyclerView.ViewHolder vh2 = recyclerView.findViewHolderForAdapterPosition(1);
            RecyclerView.ViewHolder vh3 = recyclerView.findViewHolderForAdapterPosition(2);
            RecyclerView.ViewHolder vh4 = recyclerView.findViewHolderForAdapterPosition(3);
            RecyclerView.ViewHolder vh5 = recyclerView.findViewHolderForAdapterPosition(4);

            assertNotNull(vh1);
            assertNotNull(vh2);
            assertNotNull(vh3);
            assertNotNull(vh4);
            assertNull(vh5);

            String id1 = ((SearchResultsAdapter.ViewHolder) vh1).getSearchResult().id;
            String id2 = ((SearchResultsAdapter.ViewHolder) vh2).getSearchResult().id;
            String id3 = ((SearchResultsAdapter.ViewHolder) vh3).getSearchResult().id;
            String id4 = ((SearchResultsAdapter.ViewHolder) vh4).getSearchResult().id;

            assertEquals("arctic_foxes", id1);
            assertEquals("elephant_odyssey", id2);
            assertEquals("gorillas", id3);
            assertEquals("lions", id4);
        });
    }

    @Test
    public void testInvalidQuery() {
        String searchQuery = "invalid search";
        Intent intent
                = new Intent(ApplicationProvider.getApplicationContext(), SearchResultsActivity.class);
        intent.putExtra("search_query", searchQuery);
        ActivityScenario<SearchResultsActivity> scenario = ActivityScenario.launch(intent);

        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            TextView searchQueryView = activity.findViewById(R.id.search_query_view);
            assertEquals("Results with \"" + searchQuery + "\".", searchQueryView.getText().toString());

            RecyclerView recyclerView = activity.recyclerView;

            RecyclerView.ViewHolder vh1 = recyclerView.findViewHolderForAdapterPosition(0);
            assertNull(vh1);
        });
    }
}
