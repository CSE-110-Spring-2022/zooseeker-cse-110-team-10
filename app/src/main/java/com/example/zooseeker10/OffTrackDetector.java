package com.example.zooseeker10;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Map;

public class OffTrackDetector {
    private ZooPlan plan;
    private ZooPlan.ZooWalker walker;
    private Map<String, ZooData.VertexInfo> vertexInfoMap;

    public OffTrackDetector(ZooPlan plan, ZooPlan.ZooWalker walker) {
        this.plan = plan;
        this.walker = walker;
        this.vertexInfoMap = ZooData.getVertexInfo(this); // TODO: Get context properly
    }

    public boolean isOffTrack() {
        List<String> unvisitedExhibits = walker.getUnvisitedExhibits();

        return getClosestExhibit() != unvisitedExhibits.get(0);
    }

    /**
     * TODO: Maybe unvisitedExhibits is empty
     * @return
     */
    public String getClosestExhibit() {
        List<String> unvisitedExhibits = walker.getUnvisitedExhibits();

        double minDist = Double.MIN_VALUE;
        String minDistExhibit = null;

        for (String unvistedExhibit : unvisitedExhibits) {
            double currDist = getDistanceFrom(unvistedExhibit);
            if (currDist < minDist) {
                minDist = currDist;
                minDistExhibit = unvistedExhibit;
            }
        }

        return minDistExhibit;
    }

    private double getDistanceFrom(String ID) {
        LatLng currCoord = ;  // TODO: Get curr loc of device somehow
        LatLng idCoord = vertexInfoMap.get(ID).coordinates; // TODO: Do vertexinfo properly

        return Math.pow(currCoord.latitude - idCoord.latitude, 2) +
               Math.pow(currCoord.longitude - idCoord.longitude, 2);
    }
}
