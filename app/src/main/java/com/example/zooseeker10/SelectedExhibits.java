package com.example.zooseeker10;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SelectedExhibits {

    private final Context context;
    private ArrayList<String> selectedExhibitIds;

    public SelectedExhibits(Context context) {
        this.context = context;
        this.selectedExhibitIds = new ArrayList<>();
    }

    public void addExhibit(String exhibitId) {
        if (!selectedExhibitIds.contains(exhibitId)) {
            selectedExhibitIds.add(0, exhibitId);
            Log.d("MainActivity", exhibitId);
            ((MainActivity) context).update();
        }
    }

    public ArrayList<String> getExhibitIds(){
        return this.selectedExhibitIds;
    }

    public List<ZooData.VertexInfo> getExhibitIDs() {
            Map<String, ZooData.VertexInfo> exhibits = ZooData.getVertexInfo(context);
            return this.selectedExhibitIds.stream()
                    .map(exhibits::get)
                    .collect(Collectors.toList());
    }

    public int getCount() {
        return this.selectedExhibitIds.size();
    }

    public void clear() {
        this.selectedExhibitIds = new ArrayList<>();
        Log.d("MainActivity", "CLEARED");
        ((MainActivity) context).update();
    }
}