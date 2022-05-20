package com.example.zooseeker10;

import android.content.Context;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ZooPlan implements Serializable {
    /*
    String entranceID;
    String exitID;
    Set<String> unvisited; // replanable exhibits
    */
    List<GraphPath<String, IdentifiedWeightedEdge>> plan;

    public ZooPlan(List<GraphPath<String, IdentifiedWeightedEdge>> plan) {
        this.plan = plan;
    }

    public Iterator<GraphPath<String, IdentifiedWeightedEdge>> iterator() {
        return plan.iterator();
    }

    public List<DirectionsItem> explainPath(Context context, int idx) {
        Map<String, ZooData.VertexInfo> vertexInfo = ZooData.getVertexInfo(context);
        Map<String, ZooData.EdgeInfo> edgeInfo = ZooData.getEdgeInfo(context);
        Graph<String, IdentifiedWeightedEdge> g = ZooData.getZooGraph(context);
        List<DirectionsItem> explains = new ArrayList<>();
        Iterator<String> vs = plan.get(idx).getVertexList().iterator();
        Iterator<IdentifiedWeightedEdge> es = plan.get(idx).getEdgeList().iterator();
        String lastVertex = vs.next();
        while (es.hasNext()) {
            String nextEdge = es.next().getId();
            String nextVertex = vs.next();
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

    /**
     * Summarize a list of paths as PlanDistItems (exhibit name and distance)
     *
     * @param context Context
     * @return list of PlanDistItems summarizing the path
     */
    public List<PlanDistItem> summarizePath(Context context) {
        List<PlanDistItem> items = new ArrayList<>();
        double totalLength = 0.0;
        for (GraphPath<String, IdentifiedWeightedEdge> subPath : plan) {
            String endVertexId = subPath.getEndVertex();
            Map<String, ZooData.VertexInfo> map = ZooData.getVertexInfo(context);
            String exhibitName = map.get(endVertexId).name;
            items.add(new PlanDistItem(exhibitName, totalLength + subPath.getWeight()));
            totalLength += subPath.getWeight();
        }

        return items;
    }

    public int size() {
        return plan.size();
    }
}
