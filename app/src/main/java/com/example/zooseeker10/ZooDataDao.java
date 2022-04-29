package com.example.zooseeker10;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ZooDataDao {
    @Query("SELECT * FROM `zoo_vertices` WHERE `kind`=:kind AND combinedTags LIKE :query ORDER BY `name`")
    List<ZooData.VertexInfo> getQuerySearch(ZooData.VertexInfo.Kind kind, String query);
}
