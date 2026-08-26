package com.theeztech.communityservicedashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DashboardStatAdapter extends RecyclerView.Adapter<DashboardStatAdapter.ViewHolder> {

    public static class StatItem {
        String label;
        int count;

        public StatItem(String label, int count) {
            this.label = label;
            this.count = count;
        }
    }

    private List<StatItem> statList;

    public DashboardStatAdapter(List<StatItem> statList) {
        this.statList = statList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard_stat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StatItem item = statList.get(position);
        holder.tvStatCount.setText(String.valueOf(item.count));
        holder.tvStatLabel.setText(item.label);
    }

    @Override
    public int getItemCount() {
        return statList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatCount, tvStatLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatCount = itemView.findViewById(R.id.tvStatCount);
            tvStatLabel = itemView.findViewById(R.id.tvStatLabel);
        }
    }
}
