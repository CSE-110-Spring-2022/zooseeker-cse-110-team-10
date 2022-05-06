package com.example.zooseeker10;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class DirectionsListAdapter extends RecyclerView.Adapter<DirectionsListAdapter.ViewHolder> {

    public List<String> directionsItems = Collections.emptyList();

    public void setDirectionsItems(List<String> directionsItems) {
        this.directionsItems.clear();
        this.directionsItems = directionsItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.directions_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setDirectionsItem(directionsItems.get(position));
    }

    @Override
    public int getItemCount() {
        return directionsItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView directions;
        private String item;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.directions = view.findViewById(R.id.directions);
        }

        public String getDirectionsItem() {
            return item;
        }

        public void setDirectionsItem(String item) {
            this.item = item;
            this.directions.setText(this.item);
        }
    }
}
