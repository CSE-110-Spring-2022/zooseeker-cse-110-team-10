package com.example.zooseeker10;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

@Database(entities = {ZooData.VertexInfo.class}, version = 1, exportSchema = false)
@TypeConverters({TagsConverter.class})
public abstract class ZooDatabase extends RoomDatabase {
    private static ZooDatabase singleton = null;

    public abstract ZooDataDao zooDataDao();

    public synchronized static ZooDatabase getSingleton(Context context) {
        if (singleton == null) {
            singleton = ZooDatabase.makeDatabase(context);
        }
        return singleton;
    }

    private static ZooDatabase makeDatabase(Context context) {
        return Room.databaseBuilder(context, ZooDatabase.class, "zoo_seeker.db")
                .allowMainThreadQueries()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(() -> {
                            Map<String, ZooData.VertexInfo> map
                                    = ZooData.getVertexInfo(context);
                            List<ZooData.VertexInfo> vertices = new ArrayList<>(map.values());
                            getSingleton(context).zooDataDao().insertAll(vertices);
                        });

                    }
                })
                .build();
    }
}
