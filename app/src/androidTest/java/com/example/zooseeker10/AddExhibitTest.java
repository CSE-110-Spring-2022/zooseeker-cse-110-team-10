package com.example.zooseeker10;

import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AddExhibitTest {
    @Test
    public void addExhibitTest() {
        String searchQuery = "gorillas";
        Intent intent
                = new Intent(ApplicationProvider.getApplicationContext(), SearchResultsActivity.class);
        intent.putExtra("search_query", searchQuery);
        ActivityScenario<SearchResultsActivity> scenario = ActivityScenario.launch(intent);

        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            String expectedId = "gorillas";
            RecyclerView recyclerView = activity.recyclerView;
            RecyclerView.ViewHolder vh1 = recyclerView.findViewHolderForAdapterPosition(0);

            ImageButton addBtn = vh1.itemView.findViewById(R.id.add_exhibit_button);
            addBtn.performClick();
            Log.d("Search Activity Finish Status: ", "Success");
        });
        String result=scenario.getResult().getResultData().getStringExtra("exhibitId");
        assertEquals(searchQuery, result);
    }
}
