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

    public List<DirectionsItem> directionsItems = Collections.emptyList();

    public void setDirectionsItems(List<DirectionsItem> directionsItems) {
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

    // For testing only
    public DirectionsItem getItemAt(int position) {
        return directionsItems.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView fromText;
        private final TextView toText;
        private final TextView alongText;
        private final TextView distanceText;
        private DirectionsItem item;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.fromText = view.findViewById(R.id.directions_from_text);
            this.toText = view.findViewById(R.id.directions_to_text);
            this.alongText = view.findViewById(R.id.directions_along_text);
            this.distanceText = view.findViewById(R.id.directions_distance_text);
        }

        public DirectionsItem getDirectionsItem() {
            return item;
        }

        public void setDirectionsItem(DirectionsItem item) {
            this.item = item;
            this.fromText.setText(String.format("From %s", this.item.from));
            this.alongText.setText(String.format("Along %s", this.item.street));
            this.toText.setText(String.format("To %s", this.item.to));
            this.distanceText.setText(String.format("%s ft", this.item.dist));
        }
    }
}


/*

*/