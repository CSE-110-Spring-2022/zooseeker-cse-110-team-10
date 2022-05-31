package com.example.zooseeker10;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    private ZooPlan.ZooWalker walker;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        Graph<String, IdentifiedWeightedEdge> graph = ZooData.loadZooGraphJSON(context, Globals.ZooData.ZOO_GRAPH_PATH);
        ZooData.loadVertexInfoJSON(context, Globals.ZooData.NODE_INFO_PATH);

        List<String> exhibits = Arrays.asList("siamang", "orangutan", "hippo");

        PathFinder pf = new PathFinder(graph, Globals.ZooData.ENTRANCE_GATE_ID, Globals.ZooData.EXIT_GATE_ID);

        ZooPlan plan = pf.findPath(exhibits);
        walker = plan.startWalker();
        tracker = new UserTracker(plan, walker);
    }

    @Test
    public void testGetClosestVertex() {
        // Closest to Entrance
        LatLng closestToEntrance = new LatLng(32.73387857829117, -117.14916688462237);
        tracker.setUserLocation(closestToEntrance);
        ZooData.VertexInfo vertex1 = tracker.getClosestVertex();
        assertEquals("entrance_exit_gate", vertex1.id);

        // Closest to Benchley Plaza
        LatLng closestToBenchley = new LatLng(32.74470525466686, -117.1849191694643);
        tracker.setUserLocation(closestToBenchley);
        ZooData.VertexInfo vertex2 = tracker.getClosestVertex();
        assertEquals("benchley_plaza", vertex2.id);

        // Closest to Orangutans
        LatLng closestToOrangutans = new LatLng(32.7364837608513, -117.16778352623977);
        tracker.setUserLocation(closestToOrangutans);
        ZooData.VertexInfo vertex3 = tracker.getClosestVertex();
        assertEquals("orangutan", vertex3.id);
    }

    @Test
    public void testIsOffTrack() {
        // To Siamangs
        LatLng offSiamangs = new LatLng(32.74054375850052, -117.16048822501507);
        tracker.setUserLocation(offSiamangs);
        assertTrue(tracker.isOffTrack());
        walker.traverseForward();

        // To Orangutans
        LatLng offOrangutans = new LatLng(32.73703346392607, -117.16031147291189);
        tracker.setUserLocation(offOrangutans);
        assertTrue(tracker.isOffTrack());
        walker.traverseForward();

        // To Hippos
        LatLng offHippos = new LatLng(32.73718217235696, -117.17201725988257);
        tracker.setUserLocation(offHippos);
        assertTrue(tracker.isOffTrack());
        walker.traverseForward();

        // To Exit Gate
        LatLng offExit = new LatLng(32.74580888708056, -117.1687284434164);
        tracker.setUserLocation(offExit);
        assertTrue(tracker.isOffTrack());
    }

    @Test
    public void testIsNotOffTrack() {
        // To Siamangs
        LatLng onSiamangs = new LatLng(32.73623018955072, -117.15755299529218);
        tracker.setUserLocation(onSiamangs);
        assertFalse(tracker.isOffTrack());
        walker.traverseForward();

        // To Orangutans
        LatLng onOrangutans = new LatLng(32.73617076478269, -117.16448459066062);
        tracker.setUserLocation(onOrangutans);
        assertFalse(tracker.isOffTrack());
        walker.traverseForward();

        // To Hippos
        LatLng onHippos = new LatLng(32.7403056584827, -117.16632354810152);
        tracker.setUserLocation(onHippos);
        assertFalse(tracker.isOffTrack());
        walker.traverseForward();

        // To Exit Gate
        LatLng onExit = new LatLng(32.73766925618071, -117.15934749951676);
        tracker.setUserLocation(onExit);
        assertFalse(tracker.isOffTrack());
    }

    @Test
    public void testNeedsReplan() {
        // To Siamangs, Orangutans and Hippos are the other unvisited exhibits
        // Closer to Orangutans
        LatLng parkerAviary = new LatLng(32.738934272558105, -117.1706170848883);
        tracker.setUserLocation(parkerAviary);
        assertTrue(tracker.needsReplan());

        // Closer to Hippos
        LatLng treetopsHippos = new LatLng(32.7407355186076, -117.16217814661483);
        tracker.setUserLocation(treetopsHippos);
        assertTrue(tracker.needsReplan());

        walker.traverseForward();

        // To Orangutans, Hippos are the other unvisited exhibit
        // Closer to Hippos
        LatLng capuchinMonkeys = new LatLng(32.749342396844845, -117.1668478123298);
        tracker.setUserLocation(capuchinMonkeys);
        assertTrue(tracker.needsReplan());

        tracker.setUserLocation(treetopsHippos);
        assertTrue(tracker.needsReplan());
    }

    @Test
    public void testDoesNotNeedReplan() {
        // To Siamangs, Orangutans and Hippos are the other unvisited exhibits
        // Closer to Siamangs
        LatLng frontMonkey = new LatLng(32.7410630137149, -117.15542728442142);
        tracker.setUserLocation(frontMonkey);
        assertFalse(tracker.needsReplan());

        LatLng treetopsOrangutan = new LatLng(32.73639740773089, -117.15999568674184);
        tracker.setUserLocation(treetopsOrangutan);
        assertFalse(tracker.needsReplan());

        walker.traverseForward();

        // To Orangutans, Hippos are the other unvisited exhibit
        // Closer to Orangutans
        LatLng orangutans = new LatLng(32.7364837608513, -117.16778352623977);
        tracker.setUserLocation(orangutans);
        assertFalse(tracker.needsReplan());

        LatLng owensAviary = new LatLng(32.73634157031314, -117.17237884160274);
        tracker.setUserLocation(owensAviary);
        assertFalse(tracker.needsReplan());

        walker.traverseForward();

        // To Hippos, no other unvisited exhibit
        // No replan needed for any location
        LatLng benchley = new LatLng(32.74470525466686, -117.1849191694643);
        tracker.setUserLocation(benchley);
        assertFalse(tracker.needsReplan());

        tracker.setUserLocation(frontMonkey);
        assertFalse(tracker.needsReplan());

        tracker.setUserLocation(treetopsOrangutan);
        assertFalse(tracker.needsReplan());

        tracker.setUserLocation(orangutans);
        assertFalse(tracker.needsReplan());

        tracker.setUserLocation(owensAviary);
        assertFalse(tracker.needsReplan());

        walker.traverseForward();

        // To Exit Gate, no other unvisited exhibit
        // No replan needed for any location
        tracker.setUserLocation(benchley);
        assertFalse(tracker.needsReplan());

        tracker.setUserLocation(frontMonkey);
        assertFalse(tracker.needsReplan());

        tracker.setUserLocation(treetopsOrangutan);
        assertFalse(tracker.needsReplan());

        tracker.setUserLocation(orangutans);
        assertFalse(tracker.needsReplan());

        tracker.setUserLocation(owensAviary);
        assertFalse(tracker.needsReplan());
    }
}
