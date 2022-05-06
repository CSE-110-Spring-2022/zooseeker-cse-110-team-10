package com.example.zooseeker10;

import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

import java.util.ArrayList;

public class AddExhibitsTest {

    @Test
    public void addExhibitTest() {
        String exhibitId = "lions";
        Intent intent
                = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("exhibitId", exhibitId);
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(intent);

        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            Button planButton = activity.findViewById(R.id.plan_btn);
            assertEquals(View.INVISIBLE,planButton.getVisibility());

            ArrayList<String> selectedExhibitsList = MainActivity.selectedExhibits;
            assertEquals(selectedExhibitsList.size(), 1);

           /* TextView searchQueryView = activity.findViewById(R.id.search_query_view);
            assertEquals("Results with \"" + searchQuery + "\".", searchQueryView.getText().toString());

            RecyclerView recyclerView = activity.findViewById(R.id.search_results);

            RecyclerView.ViewHolder vh1 = recyclerView.findViewHolderForAdapterPosition(0);
            RecyclerView.ViewHolder vh2 = recyclerView.findViewHolderForAdapterPosition(1);

            assertNotNull(vh1);
            assertNull(vh2);

            String id = ((SearchResultsAdapter.ViewHolder) vh1).getSearchResult().id;
            assertEquals(expectedId, id);*/
        });
    }
}
