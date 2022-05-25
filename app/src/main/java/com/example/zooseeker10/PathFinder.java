package com.example.zooseeker10;

import androidx.annotation.NonNull;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PathFinder {
    DijkstraShortestPath<String, IdentifiedWeightedEdge> gD;
    Graph<String, IdentifiedWeightedEdge> graph;
    String entranceID;
    String exitID;

    /**
     * Constructs a PathFinder object
     *
     * @param graph graph to be explored
     * @param entranceID ID of the entrance node
     * @param exitID ID of the exit node
     */
    public PathFinder(@NonNull Graph<String, IdentifiedWeightedEdge> graph, @NonNull String entranceID, @NonNull String exitID) {
        this.graph = graph;
        this.gD = new DijkstraShortestPath<>(graph);
        this.entranceID = entranceID;
        this.exitID = exitID;
    }

    /**
     * Finds a pretty bad shortest path visiting a given list of exhibits, starting at the entrance ID
     *
     * @param exhibitsToVisit the IDs of exhibits to be visited in the generated path
     * @return a pretty bad shortest path
     */
    public ZooPlan findPath(@NonNull List<String> exhibitsToVisit) {
        return findPath(exhibitsToVisit, this.entranceID);
    }

    /**
     * Finds a pretty bad shortest path visiting a given list of exhibits, starting at a given vertex
     *
     * @param exhibitsToVisit the IDs of exhibits to be visited in the generated path
     * @param from the exhibit for the plan to start from
     * @return a pretty bad shortest path
     */
    public ZooPlan findPath(@NonNull List<String> exhibitsToVisit, @NonNull String from) {
        List<GraphPath<String, IdentifiedWeightedEdge>> paths = new ArrayList<>();

        Set<String> unvisitedExhibits = new HashSet<>(exhibitsToVisit);
        String currVertex = from;

        // Traverse from current node to node in unvisitedExhibits
        while (unvisitedExhibits.size() > 0) {
            GraphPath<String, IdentifiedWeightedEdge> shortestNextPath = getShortestPathInSet(currVertex, unvisitedExhibits);

            // Updates final path
            paths.add(shortestNextPath);

            // Traversal variables updated
            String destVertex = shortestNextPath.getEndVertex();
            unvisitedExhibits.remove(destVertex);
            currVertex = destVertex;
        }

        // Traverse from current node to exit node
        paths.add(gD.getPath(currVertex, exitID));

        return new ZooPlan(paths);
    }

    /**
     * Finds the shortest path from the start vertex to some vertex in the set
     *
     * @param startVertex the vertex to search paths from
     * @param unvisitedExhibits the available destination vertices
     * @return path from specified start vertex to nearest available destination vertex
     */
    public GraphPath<String, IdentifiedWeightedEdge> getShortestPathInSet(
            String startVertex,
            Set<String> unvisitedExhibits) {
        ShortestPathAlgorithm.SingleSourcePaths<String, IdentifiedWeightedEdge> shortestPaths = gD.getPaths(startVertex);
        double shortestPathWeight = Integer.MAX_VALUE;
        String shortestPathSinkID = null;
        for (String unvisited : unvisitedExhibits) {
            double currEdgeWeight = shortestPaths.getWeight(unvisited);

            if (currEdgeWeight < shortestPathWeight) {
                shortestPathWeight = currEdgeWeight;
                shortestPathSinkID = unvisited;
            }
        }

        return shortestPaths.getPath(shortestPathSinkID);
    }
}
