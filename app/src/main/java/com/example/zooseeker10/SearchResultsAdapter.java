package com.example.zooseeker10;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {
    private List<ZooData.VertexInfo> searchResults = Collections.emptyList();
    private Consumer<ZooData.VertexInfo> onAddBtnClicked;

    public void setSearchResults(List<ZooData.VertexInfo> newSearchResults) {
        this.searchResults.clear();
        this.searchResults = newSearchResults;
        notifyDataSetChanged();
    }

    public void setOnAddBtnClickedHandler(Consumer<ZooData.VertexInfo> onAddBtnClicked) {
        this.onAddBtnClicked = onAddBtnClicked;
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
        private final ImageButton addButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.search_item_view);
            this.addButton = itemView.findViewById(R.id.add_exhibit_button);
            this.addButton.setOnClickListener(view -> {
                if (onAddBtnClicked == null) return;
                onAddBtnClicked.accept(searchResult);
            });
        }

        public ZooData.VertexInfo getSearchResult() { return this.searchResult; }

        public void setSearchResult(ZooData.VertexInfo searchResult) {
            this.searchResult = searchResult;
            String name = this.searchResult.name;

            // Add group name if exhibit has a group
            if (searchResult.hasGroup()) {
                String groupId = searchResult.groupId;
                String group = ZooData.getVertexInfo().get(groupId).name;
                name += String.format(" (%s)", group);
            }
            this.textView.setText(name);
        }
    }
}
