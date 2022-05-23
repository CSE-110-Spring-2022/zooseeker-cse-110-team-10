package com.example.zooseeker10;

import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SelectedExhibits {
    private final MainActivity mainActivity;
    public ArrayList<String> selectedExhibitIds = new ArrayList<String>();

    public SelectedExhibits(MainActivity mainActivity) {
        this.mainActivity=mainActivity;
    }

    public void selectExhibit(String exhibitId) {
        if (!selectedExhibitIds.contains(exhibitId)) {
            selectedExhibitIds.add(0, exhibitId);
            Log.d("MainActivity", exhibitId);
        }
    }

    public ArrayList<String> getExhibitIds(){
        return this.selectedExhibitIds;
    }

    public List<ZooData.VertexInfo> getExhibits(){
        Map<String, ZooData.VertexInfo> exhibits = ZooData.getVertexInfo(mainActivity);
        List<ZooData.VertexInfo> selectedExhibitsList = this.selectedExhibitIds.stream()
                .map(exhibits::get)
                .collect(Collectors.toList());
        return selectedExhibitsList;
    }
}