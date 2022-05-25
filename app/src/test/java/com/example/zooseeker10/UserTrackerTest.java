package com.example.zooseeker10;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import org.jgrapht.Graph;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class UserTrackerTest {

    private UserTracker tracker;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        Graph<String, IdentifiedWeightedEdge> graph = ZooData.loadZooGraphJSON(context, Globals.ZooData.ZOO_GRAPH_PATH); // ???
        ZooData.loadVertexInfoJSON(context, Globals.ZooData.NODE_INFO_PATH);

        List<String> exhibits = Arrays.asList("siamang", "orangutan", "hippo");

        PathFinder pf = new PathFinder(graph, Globals.ZooData.ENTRANCE_GATE_ID, Globals.ZooData.EXIT_GATE_ID);

        ZooPlan plan = pf.findPath(exhibits);
        ZooPlan.ZooWalker walker = plan.startWalker();
        tracker = new UserTracker(plan, walker);
    }

    @Test
    public void setUserLocationTest() {
        double delta = 0.000000001;
        LatLng location1 = new LatLng(20.05083, -170.56852);
        LatLng location2 = new LatLng(43.87495, -72.60192);
        LatLng location3 = new LatLng(-32.77275, 143.73982);

        tracker.setUserLocation(location1);
        assertEquals(location1.latitude, tracker.getUserLocation().latitude, delta);
        assertEquals(location1.longitude, tracker.getUserLocation().longitude, delta);

        tracker.setUserLocation(location2);
        assertEquals(location2.latitude, tracker.getUserLocation().latitude, delta);
        assertEquals(location2.longitude, tracker.getUserLocation().longitude, delta);

        tracker.setUserLocation(location3);
        assertEquals(location3.latitude, tracker.getUserLocation().latitude, delta);
        assertEquals(location3.longitude, tracker.getUserLocation().longitude, delta);
    }

    @Test
    public void getClosestVertexTest() {
        LatLng location1 = new LatLng(32.73459618734685, -117.04936);
        LatLng location2 = new LatLng(32.74576120197887, -117.18369973246877);
        LatLng location3 = new LatLng(32.735851415117665, -117.1651432637920467);

        tracker.setUserLocation(location1);
        ZooData.VertexInfo vertex1 = tracker.getClosestVertex();
        assertEquals("entrance_exit_gate", vertex1.id);

        tracker.setUserLocation(location2);
        ZooData.VertexInfo vertex2 = tracker.getClosestVertex();
        assertEquals("benchley_plaza", vertex2.id);

        tracker.setUserLocation(location3);
        ZooData.VertexInfo vertex3 = tracker.getClosestVertex();
        assertEquals("orangutan", vertex3.id);
    }
}
