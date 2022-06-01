package com.example.zooseeker10;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class PlanDistListAdapter extends RecyclerView.Adapter<PlanDistListAdapter.ViewHolder> {

    public List<PlanDistItem> planDistItems = Collections.emptyList();

    public void setPlanDistItems(List<PlanDistItem> planDistItems) {
        this.planDistItems.clear();
        this.planDistItems = planDistItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.plan_dist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setPlanDistItem(planDistItems.get(position));
    }

    public void clear(){
        planDistItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return planDistItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView exhibitName;
        private final TextView exhibitDistance;
        private PlanDistItem item;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.exhibitName = view.findViewById(R.id.exhibit_name);
            this.exhibitDistance = view.findViewById(R.id.exhibit_distance);
        }

        public PlanDistItem getPlanDistItem() {
            return item;
        }

        public void setPlanDistItem(PlanDistItem item) {
            this.item = item;
            this.exhibitName.setText(this.item.exhibitName);
            this.exhibitDistance.setText(String.format("%.0f ft", this.item.distance));
        }

    }
}
