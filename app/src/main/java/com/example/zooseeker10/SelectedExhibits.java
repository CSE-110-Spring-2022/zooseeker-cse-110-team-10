package com.example.zooseeker10;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SelectedExhibits {

    private final Context context;
    private final SelectedExhibitsObserver observer;
    private ArrayList<String> selectedExhibitIds;

    public SelectedExhibits(Context context, SelectedExhibitsObserver observer) {
        this.context = context;
        this.observer = observer;
        this.selectedExhibitIds = new ArrayList<>();
    }

    public void addExhibit(String exhibitId) {
        if (!selectedExhibitIds.contains(exhibitId)) {
            selectedExhibitIds.add(0, exhibitId);
            Log.d("MainActivity", exhibitId);
            observer.onSelectedExhibitsUpdated();
        }
    }

    public ArrayList<String> getExhibitIds(){
        return this.selectedExhibitIds;
    }

    public List<ZooData.VertexInfo> getExhibits() {
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
        observer.onSelectedExhibitsUpdated();
    }
}