package com.example.zooseeker10;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.nio.json.JSONImporter;

public class ZooData {
    public static final String ZOO_GRAPH_PATH = "zoo_graph.json";
    public static final String NODE_INFO_PATH = "exhibit_info.json";
    public static final String EDGE_INFO_PATH = "trail_info.json";
    public static final String ENTRANCE_GATE_ID = "entrance_exit_gate";
    public static final String EXIT_GATE_ID = "entrance_exit_gate";

    private static Map<String, VertexInfo> vertexInfo;
    private static Map<String, EdgeInfo> edgeInfo;
    private static Graph<String, IdentifiedWeightedEdge> zooGraph;

    @Entity(tableName = "zoo_vertices")
    public static class VertexInfo {
        public enum Kind {
            // The SerializedName annotation tells GSON how to convert
            // from the strings in our JSON to this Enum.
            @SerializedName("gate") GATE,
            @SerializedName("exhibit") EXHIBIT,
            @SerializedName("intersection") INTERSECTION,
            @SerializedName("exhibit_group") EXHIBIT_GROUP;

            public static final Set<Kind> NAVIGABLE_KINDS = Set.of(
                GATE, EXHIBIT, INTERSECTION
            );
        }

        @PrimaryKey
        @ColumnInfo(name = "id")
        @SerializedName("id")
        @NonNull
        public final String id;

        @ColumnInfo(name = "group_id")
        @SerializedName("group_id")
        @Nullable
        public final String groupId;

        @ColumnInfo(name = "kind")
        @SerializedName("kind")
        @NonNull
        public final Kind kind;

        @ColumnInfo(name = "name")
        @SerializedName("name")
        @NonNull
        public final String name;

        @ColumnInfo(name = "tags")
        @SerializedName("tags")
        @NonNull
        public final List<String> tags;

        @ColumnInfo(name = "lat")
        @SerializedName("lat")
        public final Double lat;

        @ColumnInfo(name = "lng")
        @SerializedName("lng")
        public final Double lng;

        public boolean isExhibit() {
            return kind.equals(Kind.EXHIBIT);
        }

        public boolean isIntersection() {
            return kind.equals(Kind.INTERSECTION);
        }

        public boolean isGroup() {
            return kind.equals(Kind.EXHIBIT_GROUP);
        }

        public boolean hasGroup() {
            return groupId != null;
        }

        public boolean isNavigable() {
            return Kind.NAVIGABLE_KINDS.contains(kind);
        }

        public VertexInfo(@NonNull String id,
                       @Nullable String groupId,
                       @NonNull Kind kind,
                       @NonNull String name,
                       @NonNull List<String> tags,
                       @Nullable Double lat,
                       @Nullable Double lng) {
            this.id = id;
            this.groupId = groupId;
            this.kind = kind;
            this.name = name;
            this.tags = tags;
            this.lat = lat;
            this.lng = lng;

            if (!this.hasGroup() && (lat == null || lng == null)) {
                throw new RuntimeException("Nodes must have a lat/long unless they are grouped.");
            }
        }

        @Override
        public String toString() {
            return "VertexInfo{" +
                    "id='" + id + '\'' +
                    ", kind=" + kind +
                    ", name='" + name + '\'' +
                    ", tags=" + tags +
                    '}';
        }
    }

    public static class EdgeInfo {
        public String id;
        public String street;

        public EdgeInfo(String id, String street) {
            this.id = id;
            this.street = street;
        }

        @Override
        public String toString() {
            return "EdgeInfo{" +
                    "id='" + id + '\'' +
                    ", street='" + street + '\'' +
                    '}';
        }
    }

    public static Map<String, ZooData.VertexInfo> getVertexInfo(Context context) {
        if (vertexInfo == null) {
            vertexInfo = loadVertexInfoJSON(context, NODE_INFO_PATH);
        }
        return vertexInfo;
    }

    public static Map<String, ZooData.VertexInfo> loadVertexInfoJSON(Context context, String path) {
        try {
            InputStream inputStream = context.getAssets().open(path);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            Type type = new TypeToken<List<ZooData.VertexInfo>>(){}.getType();

            List<ZooData.VertexInfo> zooData = gson.fromJson(reader, type);

            vertexInfo = zooData
                    .stream()
                    .collect(Collectors.toMap(v -> v.id, datum -> datum));

            return vertexInfo;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    public static Map<String, ZooData.EdgeInfo> getEdgeInfo(Context context) {
        if (edgeInfo == null) {
            edgeInfo = loadEdgeInfoJSON(context, EDGE_INFO_PATH);
        }
        return edgeInfo;
    }

    public static Map<String, ZooData.EdgeInfo> loadEdgeInfoJSON(Context context, String path) {
        try {
            InputStream inputStream = context.getAssets().open(path);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            Type type = new TypeToken<List<ZooData.EdgeInfo>>(){}.getType();

            List<ZooData.EdgeInfo> zooData = gson.fromJson(reader, type);

            edgeInfo = zooData
                    .stream()
                    .collect(Collectors.toMap(v -> v.id, datum -> datum));

            return edgeInfo;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    public static Graph<String, IdentifiedWeightedEdge> getZooGraph(Context context) {
        if (zooGraph == null) {
            zooGraph = loadZooGraphJSON(context, ZOO_GRAPH_PATH);
        }
        return zooGraph;
    }

    public static Graph<String, IdentifiedWeightedEdge> loadZooGraphJSON(Context context, String path) {
        zooGraph = new DefaultUndirectedWeightedGraph<>(IdentifiedWeightedEdge.class);

        // Create an importer that can be used to populate our empty graph.
        JSONImporter<String, IdentifiedWeightedEdge> importer = new JSONImporter<>();

        // We don't need to convert the vertices in the graph, so we return them as is.
        importer.setVertexFactory(v -> v);

        // We need to make sure we set the IDs on our edges from the 'id' attribute.
        // While this is automatic for vertices, it isn't for edges. We keep the
        // definition of this in the IdentifiedWeightedEdge class for convenience.
        importer.addEdgeAttributeConsumer(IdentifiedWeightedEdge::attributeConsumer);

        try {
            InputStream inputStream = context.getAssets().open(path);
            Reader reader = new InputStreamReader(inputStream);

            importer.importGraph(zooGraph, reader);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return zooGraph;
    }
}