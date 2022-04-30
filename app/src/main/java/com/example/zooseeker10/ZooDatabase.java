package com.example.zooseeker10;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {ZooData.VertexInfo.class}, version = 1, exportSchema = false)
@TypeConverters({TagsConverter.class})
public abstract class ZooDatabase extends RoomDatabase {
    public abstract ZooDataDao zooDataDao();
}
