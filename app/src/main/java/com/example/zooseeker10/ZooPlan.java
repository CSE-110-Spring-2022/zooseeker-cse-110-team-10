package com.example.zooseeker10;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ZooPlan is used to easily manage and manipulate a plan
 */
public class ZooPlan implements Serializable {
    ArrayList<GraphPath<String, IdentifiedWeightedEdge>> plan;

    /**
     * ZooWalker is used to keep track of the current position of the user
     * within the ZooPath
     */
    public class ZooWalker implements Serializable {
        private int currentIndex;

        /**
         * Constructs a new ZooWalker starting at startIndex
         *
         * @param startIndex starting index of the ZooWalker
         */
        public ZooWalker(int startIndex) {
            this.currentIndex = startIndex;
        }

        /**
         * Determines whether there is another exhibit left in the path
         *
         * @return true if there is a next exhibit, false otherwise
         */
        public boolean hasNext() {
            return currentIndex < plan.size() - 1;
        }

        /**
         * Determines whether there is an exhibit preceding the current exhibit within the path
         *
         * @return true if there is a preceding exhibit, false otherwise
         */
        public boolean hasPrevious() {
            return currentIndex > 0;
        }

        /**
         * Moves the walker forward one position in the path if there is space
         *
         * @return true if the walker moved forwards, false otherwise
         */
        public boolean traverseForward() {
            boolean hadNext = hasNext();

            if (hadNext) {
                currentIndex++;
            }
            else {
                Log.w("ZooPlan", "traverseForward called with no next");
            }

            return hadNext;
        }

        /**
         * Moves the walker backward one position in the path if there is space
         *
         * @return true if the walker moved backwards, false otherwise
         */
        public boolean traverseBackward() {
            boolean hadPrevious = hasPrevious();

            if (hadPrevious) {
                currentIndex--;
            }
            else {
                Log.w("ZooPlan", "traverseBackward called with no previous");
            }

            return hadPrevious;
        }

        /**
         * Gets the current position of the user within the ZooPath
         *
         * @return current index of user in the path
         */
        public int getCurrentExhibitIndex() {
            return currentIndex;
        }

        public String getCurrentExhibitID() {
            return plan.get(currentIndex).getStartVertex();
        }

        public List<String> getUnvisitedExhibits() {
            List<String> unvisited = new ArrayList<>();

            for (int i = currentIndex; i < plan.size(); i++) {
                unvisited.add(plan.get(i).getEndVertex());
            }

            return unvisited;
        }

        /**
         * Converts the current subpath to a list of DirectionsItems
         * to be used in DirectionsActivity.
         *
         * @param context the current context
         * @return list of DirectionsItem for the current subpath
         */
        public List<DirectionsItem> explainPath(Context context) {
            Map<String, ZooData.VertexInfo> vertexInfo = ZooData.getVertexInfo(context);
            Map<String, ZooData.EdgeInfo> edgeInfo = ZooData.getEdgeInfo(context);
            Graph<String, IdentifiedWeightedEdge> g = ZooData.getZooGraph(context);
            List<DirectionsItem> explains = new ArrayList<>();
            Iterator<String> vs = plan.get(currentIndex).getVertexList().iterator();
            Iterator<IdentifiedWeightedEdge> es = plan.get(currentIndex).getEdgeList().iterator();
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
    }

    /**
     * Constructs a ZooPlan object
     *
     * @param plan plan to be managed
     */
    public ZooPlan(List<GraphPath<String, IdentifiedWeightedEdge>> plan) {
        this.plan = new ArrayList<>(plan);
    }

    /**
     * Gets an iterator for the underlying path
     *
     * @return an iterator for the underlying path
     */
    public Iterator<GraphPath<String, IdentifiedWeightedEdge>> iterator() {
        return plan.iterator();
    }

    /**
     * Creates a new ZooWalker viewing the start of the path
     *
     * @return new ZooWalker viewing the start of the path
     */
    public ZooWalker startWalker() {
        return new ZooWalker(0);
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

    /**
     * Gets the size of the plan
     *
     * @return size of the plan
     */
    public int size() {
        return plan.size();
    }

    /**
     * Gets the exhibits that can be re-planned, i.e. the destination exhibit for the subpath
     * represented by the iterator and all subsequent exhibits
     */
    public List<String> getReplannable(ZooWalker walker) {
        List<String> replannableExhibits = new ArrayList<>();
        for (int i = walker.currentIndex; i < this.plan.size() - 1; i++) {
            replannableExhibits.add(this.plan.get(i).getEndVertex());
        }
        return replannableExhibits;
    }

    /**
     * Overwrites part of this plan with a new plan. The subpath represented by the iterator and
     * all subsequent subpaths are overwritten.
     *
     * @param walker iterator for the first subpath to be overwritten by the new plan
     * @param newPlan the new plan to be written over this plan
     */
    public void replan(ZooWalker walker, ZooPlan newPlan) {
        int i = walker.currentIndex;
        List<GraphPath<String, IdentifiedWeightedEdge>> replanSegment = this.plan.subList(i, this.plan.size());
        replanSegment.clear();
        replanSegment.addAll(newPlan.plan);
    }
}
