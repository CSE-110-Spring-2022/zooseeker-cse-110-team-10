package com.example.zooseeker10;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

public class OffTrackDetector {
    private ZooPlan plan;
    private ZooPlan.ZooWalker walker;
    private Map<String, ZooData.VertexInfo> vertexInfoMap;

    /**
     * Constructs a new OffTrackDetector
     *
     * @param context context of the calling activity
     * @param plan a plan we want to traverse
     * @param walker a walker generated from the plan we want to monitor
     */
    public OffTrackDetector(Context context, ZooPlan plan, ZooPlan.ZooWalker walker) {
        this.plan = plan;
        this.walker = walker;
        this.vertexInfoMap = ZooData.getVertexInfo(context);
    }

    /**
     * Determines whether the device is off track
     *
     * @param currentLocation location of the device
     * @return true if the current location is closer to an exhibit later in the plan compared to
     *         the next exhibit, otherwise false
     */
    public boolean isOffTrack(LatLng currentLocation) {
        double distToCurrExhibit = getDistanceFrom(currentLocation, walker.getCurrentExhibitID());

        for (String unvisitedExhibit : walker.getUnvisitedExhibits()) {
            if (getDistanceFrom(currentLocation, unvisitedExhibit) < distToCurrExhibit) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the distance from a location to an exhibit
     *
     * @param currentLocation current location of the device
     * @param exhibitID ID of the exhibit to calculate the distance to
     * @return distance to the given exhibit
     */
    private double getDistanceFrom(LatLng currentLocation, String exhibitID) {
        LatLng idCoord = vertexInfoMap.get(ID).coordinates; // TODO: Do vertexinfo properly

        return Math.pow(currentLocation.latitude - idCoord.latitude, 2) +
               Math.pow(currentLocation.longitude - idCoord.longitude, 2);
    }
}
