package com.example.zooseeker10;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class SelectedExhibitsAdapter extends RecyclerView.Adapter<SelectedExhibitsAdapter.ViewHolder> {
    private List<ZooData.VertexInfo> selectedExhibits = Collections.emptyList();


    public void setSelectedExhibits(List<ZooData.VertexInfo> newSelectedExhibits) {
        this.selectedExhibits.clear();
        this.selectedExhibits = newSelectedExhibits;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.selected_exhibits_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setSelectedExhibit(this.selectedExhibits.get(position));
    }

    public void clear() {
        selectedExhibits.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.selectedExhibits.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private ZooData.VertexInfo selectedExhibit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.selected_exhibit_view);
        }

        public ZooData.VertexInfo getSelectedExhibit() { return this.selectedExhibit; }

        public void setSelectedExhibit(ZooData.VertexInfo selectedExhibit) {
            this.selectedExhibit = selectedExhibit;
            this.textView.setText(this.selectedExhibit.name);
        }
    }
}
