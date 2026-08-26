package com.theeztech.communityservicedashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ApproveHealthcareAdapter extends RecyclerView.Adapter<ApproveHealthcareAdapter.ViewHolder> {

    private List<HealthcareCenter> healthcareList;
    private OnHealthcareApprovalListener listener;

    public interface OnHealthcareApprovalListener {
        void onApprove(HealthcareCenter center);
        void onReject(HealthcareCenter center);
    }

    public ApproveHealthcareAdapter(List<HealthcareCenter> healthcareList, OnHealthcareApprovalListener listener) {
        this.healthcareList = healthcareList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_approve_healthcare, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HealthcareCenter center = healthcareList.get(position);
        holder.tvName.setText(center.getName());
        holder.tvType.setText(center.getType());
        holder.tvContact.setText("Contact: " + center.getContact());
        holder.tvEmail.setText("Email: " + center.getEmail());
        holder.tvAddress.setText("Address: " + center.getAddress());
        holder.tvDesc.setText(center.getDescription());

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(center));
        holder.btnReject.setOnClickListener(v -> listener.onReject(center));
    }

    @Override
    public int getItemCount() {
        return healthcareList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvContact, tvEmail, tvAddress, tvDesc;
        MaterialButton btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvHealthcareName);
            tvType = itemView.findViewById(R.id.tvType);
            tvContact = itemView.findViewById(R.id.tvContactInfo);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvDesc = itemView.findViewById(R.id.tvDescription);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
