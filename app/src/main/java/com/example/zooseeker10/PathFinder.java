package com.example.zooseeker10;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.GraphWalk;

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
    public PathFinder(Graph<String, IdentifiedWeightedEdge> graph, String entranceID, String exitID) {
        this.graph = graph;
        this.gD = new DijkstraShortestPath<>(graph);
        this.entranceID = entranceID;
        this.exitID = exitID;
    }

    /**
     * Finds a pretty bad shortest path given a list of exhibits that must be visited
     *
     * @param exhibitsToVisit the IDs of exhibits to be visited in the generated path
     * @return a pretty bad shortest path
     */
    public GraphPath<String, IdentifiedWeightedEdge> findPath(List<String> exhibitsToVisit) {
        List<String> vertices = new ArrayList<>();
        List<IdentifiedWeightedEdge> edges = new ArrayList<>();

        Set<String> unvisitedExhibits = new HashSet<>(exhibitsToVisit);
        String currVertex = entranceID;
        double weight = 0;

        // Traverse from current node to node in unvisitedExhibits
        while (unvisitedExhibits.size() > 0) {
            GraphPath<String, IdentifiedWeightedEdge> shortestNextPath = getShortestPathInSet(currVertex, unvisitedExhibits);

            // Updates final path
            vertices.addAll(shortestNextPath.getVertexList()); //TODO: make sure not double adding
            edges.addAll(shortestNextPath.getEdgeList()); //TODO: Same as above

            // Traversal variables updated
            String destVertex = shortestNextPath.getEndVertex();
            unvisitedExhibits.remove(destVertex);
            currVertex = destVertex;
            weight += shortestNextPath.getWeight();
        }

        // Traverse from current node to exit node
        GraphPath<String, IdentifiedWeightedEdge> vertexToEndPath = gD.getPath(currVertex, exitID);
        vertices.addAll(vertexToEndPath.getVertexList());
        edges.addAll(vertexToEndPath.getEdgeList());

        return new GraphWalk<>(graph, entranceID, exitID, vertices, edges, weight);
    }

    /**
     * Finds the shortest path from the start vertex to some vertex in the set
     *
     * @param startVertex the vertex to search paths from
     * @param unvisitedExhibits the available destination vertices
     * @return path from specified start vertex to nearest available destination vertex
     */
    private GraphPath<String, IdentifiedWeightedEdge> getShortestPathInSet(
            String startVertex,
            Set<String> unvisitedExhibits) {
        ShortestPathAlgorithm.SingleSourcePaths<String, IdentifiedWeightedEdge> shortestPaths = gD.getPaths(startVertex);
        double shortestPathWeight = Integer.MIN_VALUE;
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
