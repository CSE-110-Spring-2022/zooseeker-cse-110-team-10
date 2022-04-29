package com.example.zooseeker10;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ZooData.VertexInfo.class}, version = 1)
public abstract class ZooDatabase extends RoomDatabase {
    public abstract ZooDataDao zooDataDao();
}
