package com.example.zooseeker10;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {
    private List<ZooData.VertexInfo> searchResults = Collections.emptyList();

    public void setSearchResults(List<ZooData.VertexInfo> newSearchResults) {
        this.searchResults.clear();
        this.searchResults = newSearchResults;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.search_results_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setSearchResult(this.searchResults.get(position));
    }

    @Override
    public int getItemCount() {
        return this.searchResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private ZooData.VertexInfo searchResult;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.search_item_view);
        }

        public ZooData.VertexInfo getSearchResult() { return this.searchResult; }

        public void setSearchResult(ZooData.VertexInfo searchResult) {
            this.searchResult = searchResult;
            this.textView.setText(this.searchResult.name);
        }
    }
}
