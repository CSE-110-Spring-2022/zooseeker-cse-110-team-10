package com.example.zooseeker10;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ZooDataDao {

    @Insert
    void insert(ZooData.VertexInfo vertexInfo);

    @Insert
    void insertAll(List<ZooData.VertexInfo> vertexInfoList);

    @Query("SELECT * FROM `zoo_vertices` WHERE `id`=:id")
    ZooData.VertexInfo get(String id);

    @Query("SELECT * FROM `zoo_vertices` ORDER BY `name`")
    List<ZooData.VertexInfo> getAll();

    /**
     * Citation:
     * https://www.sqlitetutorial.net/sqlite-like/
     * https://www.sqltutorial.org/sql-string-functions/sql-lower/
     * SQLite LIKE and SQL LOWER: Convert a String Into Lowercase
     * May 8 2022
     * Used a reference for how to get items with matching normalized substrings.
     * A.S.
     */
    @Query("SELECT * FROM `zoo_vertices` WHERE `kind`=:kind AND" +
            " (LOWER(`tags`) LIKE LOWER(:query) OR LOWER(`name`) LIKE LOWER(:query))  ORDER BY `name`")
    List<ZooData.VertexInfo> getQuerySearch(ZooData.VertexInfo.Kind kind, String query);
}
