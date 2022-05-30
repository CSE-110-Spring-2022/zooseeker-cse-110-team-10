package com.example.zooseeker10;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class PlanPathTest {
    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        Globals.ZooDataTest.setLegacy(context);
    }

    @Test
    public void planButtonInvisible() {
        ActivityScenario<SelectionActivity> scenario
                = ActivityScenario.launch(SelectionActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            assertEquals(View.INVISIBLE, activity.findViewById(R.id.plan_btn).getVisibility());
        });
    }

    @Test
    public void planButtonVisible() {
        ActivityScenario<SelectionActivity> scenario
                = ActivityScenario.launch(SelectionActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            activity.selectedExhibits.addExhibit("gators");
            assertEquals(View.VISIBLE, activity.findViewById(R.id.plan_btn).getVisibility());
        });
    }

    @Test
    public void testPlan() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PlanActivity.class);
        ArrayList<String> exhibits = new ArrayList<>();
        exhibits.add("arctic_foxes");
        exhibits.add("gorillas");
        intent.putStringArrayListExtra("exhibits", exhibits);
        ActivityScenario<PlanActivity> scenario
                = ActivityScenario.launch(intent);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView rv = activity.findViewById(R.id.plan_dist_list);
            RecyclerView.Adapter ad = rv.getAdapter();
            assertEquals(3, ad.getItemCount());
            RecyclerView.ViewHolder vh = rv.findViewHolderForAdapterPosition(0);
            assertEquals("Gorillas", ((TextView)vh.itemView.findViewById(R.id.exhibit_name)).getText());
            assertEquals("210 ft", ((TextView)vh.itemView.findViewById(R.id.exhibit_distance)).getText());
            vh = rv.findViewHolderForAdapterPosition(1);
            assertEquals("Arctic Foxes", ((TextView)vh.itemView.findViewById(R.id.exhibit_name)).getText());
            assertEquals("710 ft", ((TextView)vh.itemView.findViewById(R.id.exhibit_distance)).getText());
            vh = rv.findViewHolderForAdapterPosition(2);
            assertEquals("Entrance and Exit Gate", ((TextView)vh.itemView.findViewById(R.id.exhibit_name)).getText());
            assertEquals("1020 ft", ((TextView)vh.itemView.findViewById(R.id.exhibit_distance)).getText());
        });
    }
}