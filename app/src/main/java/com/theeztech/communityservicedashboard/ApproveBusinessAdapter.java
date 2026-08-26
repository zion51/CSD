package com.theeztech.communityservicedashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ApproveBusinessAdapter extends RecyclerView.Adapter<ApproveBusinessAdapter.ViewHolder> {

    private List<Business> businessList;
    private OnBusinessApprovalListener listener;

    public interface OnBusinessApprovalListener {
        void onApprove(Business business);
        void onReject(Business business);
    }

    public ApproveBusinessAdapter(List<Business> businessList, OnBusinessApprovalListener listener) {
        this.businessList = businessList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_approve_business, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Business business = businessList.get(position);
        holder.tvName.setText(business.getName());
        holder.tvCategory.setText(business.getCategory());
        holder.tvOwner.setText("Owner: " + business.getOwnerName() + " (" + business.getMobile() + ")");
        holder.tvAddress.setText("Address: " + business.getAddress());
        holder.tvDesc.setText(business.getDescription());

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(business));
        holder.btnReject.setOnClickListener(v -> listener.onReject(business));
    }

    @Override
    public int getItemCount() {
        return businessList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvOwner, tvAddress, tvDesc;
        MaterialButton btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvBusinessName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvOwner = itemView.findViewById(R.id.tvOwnerInfo);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvDesc = itemView.findViewById(R.id.tvDescription);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
