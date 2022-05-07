package com.example.zooseeker10;

import android.content.Context;
import androidx.annotation.NonNull;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
     * Finds a pretty bad shortest path given a list of exhibits that must be visited
     *
     * @param exhibitsToVisit the IDs of exhibits to be visited in the generated path
     * @return a pretty bad shortest path
     */
    public List<GraphPath<String, IdentifiedWeightedEdge>> findPath(@NonNull List<String> exhibitsToVisit) {
        List<GraphPath<String, IdentifiedWeightedEdge>> paths = new ArrayList<>();

        Set<String> unvisitedExhibits = new HashSet<>(exhibitsToVisit);
        String currVertex = entranceID;
        double weight = 0;

        // Traverse from current node to node in unvisitedExhibits
        while (unvisitedExhibits.size() > 0) {
            GraphPath<String, IdentifiedWeightedEdge> shortestNextPath = getShortestPathInSet(currVertex, unvisitedExhibits);

            // Updates final path
            paths.add(shortestNextPath);

            // Traversal variables updated
            String destVertex = shortestNextPath.getEndVertex();
            unvisitedExhibits.remove(destVertex);
            currVertex = destVertex;
            weight += shortestNextPath.getWeight();
        }

        // Traverse from current node to exit node
        paths.add(gD.getPath(currVertex, exitID));

        return paths;
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

    public static List<DirectionsItem> explainPath(Context context, List<String> path) {
        Map<String, ZooData.VertexInfo> vertexInfo = ZooData.loadVertexInfoJSON(context, ZooData.NODE_INFO_PATH);
        Map<String, ZooData.EdgeInfo> edgeInfo = ZooData.loadEdgeInfoJSON(context, ZooData.EDGE_INFO_PATH);
        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(context, ZooData.ZOO_GRAPH_PATH);
        List<DirectionsItem> explains = new ArrayList<>();
        Iterator<String> parts = path.iterator();
        String lastVertex = parts.next();
        while (parts.hasNext()) {
            String nextEdge = parts.next();
            String nextVertex = parts.next();
            DirectionsItem explain = new DirectionsItem(
                    vertexInfo.get(lastVertex).name,
                    vertexInfo.get(nextVertex).name,
                    edgeInfo.get(nextEdge).street,
                    g.getEdgeWeight(g.getEdge(lastVertex, nextVertex))
            );
            explains.add(explain);
            lastVertex = nextVertex;
        }
        return explains;
    }
}
