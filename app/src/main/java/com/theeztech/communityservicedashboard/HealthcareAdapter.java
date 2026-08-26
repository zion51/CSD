package com.theeztech.communityservicedashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HealthcareAdapter extends RecyclerView.Adapter<HealthcareAdapter.ViewHolder> {

    private List<HealthcareCenter> healthcareList;
    private Context context;
    private OnCenterClickListener listener;

    public interface OnCenterClickListener {
        void onCenterClick(HealthcareCenter center);
    }

    public HealthcareAdapter(Context context, List<HealthcareCenter> healthcareList, OnCenterClickListener listener) {
        this.context = context;
        this.healthcareList = healthcareList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_healthcare, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HealthcareCenter center = healthcareList.get(position);
        holder.tvName.setText(center.getName());
        holder.tvType.setText(center.getType());
        holder.tvAddress.setText(center.getAddress());
        holder.tvContact.setText(center.getContact());
        
        holder.itemView.setOnClickListener(v -> listener.onCenterClick(center));
    }

    @Override
    public int getItemCount() {
        return healthcareList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvAddress, tvContact;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvHealthcareName);
            tvType = itemView.findViewById(R.id.tvHealthcareType);
            tvAddress = itemView.findViewById(R.id.tvHealthcareAddress);
            tvContact = itemView.findViewById(R.id.tvHealthcareContact);
        }
    }
}
