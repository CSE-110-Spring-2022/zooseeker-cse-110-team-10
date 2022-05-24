package com.example.zooseeker10;

import android.content.Context;

import java.util.List;

public class SearchResults {
    private List<ZooData.VertexInfo> searchResults;

    public SearchResults(Context context, String searchQuery) {
        ZooDataDao zooDataDao = ZooDatabase.getSingleton(context).zooDataDao();
        this.searchResults = zooDataDao
            .getQuerySearch(ZooData.VertexInfo.Kind.EXHIBIT, '%' + searchQuery + '%');
    }

    List<ZooData.VertexInfo> getSearchResults() {
        return this.searchResults;
    }
}