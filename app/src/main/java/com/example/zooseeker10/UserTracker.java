package com.example.zooseeker10;

import androidx.annotation.VisibleForTesting;

import com.google.android.gms.maps.model.LatLng;

import org.jgrapht.GraphPath;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserTracker {
    private ZooPlan plan;
    private ZooPlan.ZooWalker walker;
    private Map<String, ZooData.VertexInfo> vertexInfoMap;
    private LatLng userLocation;

    /**
     * Constructs a new UserTracker
     *
     * @param plan a plan we want to traverse
     * @param walker a walker generated from the plan we want to monitor
     */
    public UserTracker(ZooPlan plan, ZooPlan.ZooWalker walker) {
        this.plan = plan;
        this.walker = walker;
        this.vertexInfoMap = ZooData.getVertexInfo();

        // Set default to Entrance and Exit Gate
        this.userLocation = LatLngs.DEFAULT_LOCATION;
    }

    public LatLng getUserLocation() {
        return this.userLocation;
    }

    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
    }

    public ZooData.VertexInfo getClosestVertex() {
        ZooData.VertexInfo closestVertex = null;
        double minDistance = Double.MAX_VALUE;

        for (var vertex : vertexInfoMap.values()) {
            if (vertex.hasGroup()) {
                continue;
            }
            double currentDistance = getDistance(userLocation, vertex);
            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                closestVertex = vertex;
            }
        }

        return closestVertex;
    }

    /**
     * Determines whether the device is off track enough to replan
     *
     * @return true if the current location is closer to an exhibit later in the plan compared to
     *         the next exhibit, otherwise false
     */
    public boolean needsReplan() {
        PathFinder pf =
            new PathFinder(ZooData.getZooGraph(), Globals.ZooData.ENTRANCE_GATE_ID, Globals.ZooData.EXIT_GATE_ID);

        ZooData.VertexInfo currentVertex = getClosestVertex();
        Set<String> unvisitedExhibits = new HashSet<>(plan.getReplannable(walker));
        if (unvisitedExhibits.isEmpty()) {
            return false;
        }
        GraphPath<String, IdentifiedWeightedEdge> path = pf.getShortestPathInSet(currentVertex.id, unvisitedExhibits);

        return !path.getEndVertex().equals(walker.getCurrentPath().getEndVertex());
    }

    /**
     * Determines whether a user
     *
     * @return whether a user is off-track
     */
    public boolean isOffTrack() {
        String closestVertexId = this.getClosestVertex().id;
        boolean onPath = walker.getCurrentPath().getVertexList().contains(closestVertexId);
        return !onPath;
    }

    /**
     * Calculates the distance from a location to an exhibit
     *
     * @param currentLocation current location of the device
     * @param vertexInfo the exhibit to calculate the distance to
     * @return distance to the given exhibit
     */
    private double getDistance(LatLng currentLocation, ZooData.VertexInfo vertexInfo) {
        LatLng exhibitCoord = new LatLng(vertexInfo.lat, vertexInfo.lng);

        return Math.sqrt(
            Math.pow(
                LatLngs.LAT_TO_FT * (currentLocation.latitude - exhibitCoord.latitude), 2) +
            Math.pow(
                LatLngs.LNG_TO_FT * (currentLocation.longitude - exhibitCoord.longitude), 2)
        );
    }
}
