package com.theeztech.communityservicedashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

    private List<HealthcareService> services;
    private OnServiceActionListener listener;
    private boolean isViewOnly = false;

    public interface OnServiceActionListener {
        void onEdit(HealthcareService service);
        void onDelete(HealthcareService service);
    }

    public ServiceAdapter(List<HealthcareService> services, OnServiceActionListener listener) {
        this.services = services;
        this.listener = listener;
        this.isViewOnly = false;
    }

    public ServiceAdapter(List<HealthcareService> services) {
        this.services = services;
        this.isViewOnly = true;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HealthcareService service = services.get(position);
        holder.tvName.setText(service.getName());
        holder.tvDesc.setText(service.getDescription());
        holder.tvPrice.setText("Price: " + service.getPrice());

        if (isViewOnly) {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(service);
            });
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(service);
            });
        }
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvPrice;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvServiceName);
            tvDesc = itemView.findViewById(R.id.tvServiceDesc);
            tvPrice = itemView.findViewById(R.id.tvServicePrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
