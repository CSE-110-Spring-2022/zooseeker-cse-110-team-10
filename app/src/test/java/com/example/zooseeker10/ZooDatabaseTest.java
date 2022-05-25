package com.example.zooseeker10;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class ZooDatabaseTest {
    private ZooDataDao dao;
    private ZooDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        Globals.ZooDataTest.setLegacy(context);

        db = Room.inMemoryDatabaseBuilder(context, ZooDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.zooDataDao();

        fillDatabase();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    private void fillDatabase() {
        Context context = ApplicationProvider.getApplicationContext();

        Map<String, ZooData.VertexInfo> map = ZooData.getVertexInfo(context);
        List<ZooData.VertexInfo> vertices = new ArrayList<>(map.values());

        dao.insertAll(vertices);
    }

    @Test
    public void testInsert() {
        List<ZooData.VertexInfo> allVertices = dao.getAll();

        ZooData.VertexInfo newVertex =
                new ZooData.VertexInfo("test-id", "test-group", ZooData.VertexInfo.Kind.EXHIBIT, "Test", new ArrayList<>(), 0.0, 0.0);

        dao.insert(newVertex);

        assertNotNull(dao.get("test-id"));
        assertEquals(allVertices.size() + 1, dao.getAll().size());
    }

    @Test
    public void testGet() {
        ZooData.VertexInfo invalid = dao.get("gwegas");
        assertNull(invalid);

        ZooData.VertexInfo gate = dao.get("entrance_exit_gate");
        assertNotNull(gate);
        assertEquals("entrance_exit_gate", gate.id);
        assertEquals("Entrance and Exit Gate", gate.name);
        assertEquals(ZooData.VertexInfo.Kind.GATE, gate.kind);
        assertEquals(Arrays.asList("enter", "leave", "start", "begin", "entrance", "exit"), gate.tags);
    }

    @Test
    public void testGetAll() {
        List<ZooData.VertexInfo> allVertices = dao.getAll();

        assertEquals(7, allVertices.size());
        assertEquals("Alligators", allVertices.get(0).name);
        assertEquals("Arctic Foxes", allVertices.get(1).name);
        assertEquals("Elephant Odyssey", allVertices.get(2).name);
        assertEquals("Entrance Plaza", allVertices.get(3).name);
        assertEquals("Entrance and Exit Gate", allVertices.get(4).name);
        assertEquals("Gorillas", allVertices.get(5).name);
        assertEquals("Lions", allVertices.get(6).name);
    }

    @Test
    public void testSearchValidName() {
        List<ZooData.VertexInfo> alligators = dao.getQuerySearch(ZooData.VertexInfo.Kind.EXHIBIT, "%liGaTO%");
        List<ZooData.VertexInfo> elephants = dao.getQuerySearch(ZooData.VertexInfo.Kind.EXHIBIT, "%elephant odyssey%");

        assertEquals(1, alligators.size());
        assertEquals(1, elephants.size());

        ZooData.VertexInfo alligator = alligators.get(0);
        ZooData.VertexInfo elephant = elephants.get(0);

        assertEquals("gators", alligator.id);
        assertEquals(ZooData.VertexInfo.Kind.EXHIBIT, alligator.kind);
        assertEquals("Alligators", alligator.name);
        assertEquals(Arrays.asList("alligator", "reptile", "gator"), alligator.tags);

        assertEquals("elephant_odyssey", elephant.id);
        assertEquals(ZooData.VertexInfo.Kind.EXHIBIT, elephant.kind);
        assertEquals("Elephant Odyssey", elephant.name);
        assertEquals(Arrays.asList("elephant", "mammal", "africa"), elephant.tags);
    }

    @Test
    public void testSearchInvalidNameOrTag() {
        List<ZooData.VertexInfo> invalid = dao.getQuerySearch(ZooData.VertexInfo.Kind.EXHIBIT, "%asgawef%");

        assertEquals(0, invalid.size());
    }

    @Test
    public void testSearchValidTag() {
        List<ZooData.VertexInfo> mammals = dao.getQuerySearch(ZooData.VertexInfo.Kind.EXHIBIT, "%mammal%");
        List<ZooData.VertexInfo> apes = dao.getQuerySearch(ZooData.VertexInfo.Kind.EXHIBIT, "%ape%");

        assertEquals(4, mammals.size());
        assertEquals(1, apes.size());

        assertEquals("Arctic Foxes", mammals.get(0).name);
        assertEquals("Elephant Odyssey", mammals.get(1).name);
        assertEquals("Gorillas", mammals.get(2).name);
        assertEquals("Lions", mammals.get(3).name);

        assertEquals("Gorillas", apes.get(0).name);

    }
}
