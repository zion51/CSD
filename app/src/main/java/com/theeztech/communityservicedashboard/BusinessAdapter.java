package com.theeztech.communityservicedashboard;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.ViewHolder> {

    private Context context;
    private List<Business> businessList;

    public BusinessAdapter(Context context, List<Business> businessList) {
        this.context = context;
        this.businessList = businessList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_business, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Business business = businessList.get(position);
        holder.tvName.setText(business.getName());
        holder.tvCategory.setText(business.getCategory());
        holder.tvOwner.setText("Owner: " + business.getOwnerName());
        holder.tvAddress.setText("Address: " + business.getAddress());
        holder.tvDescription.setText(business.getDescription());

        holder.btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + business.getMobile()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return businessList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvOwner, tvAddress, tvDescription;
        MaterialButton btnCall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvBusinessName);
            tvCategory = itemView.findViewById(R.id.tvBusinessCategory);
            tvOwner = itemView.findViewById(R.id.tvBusinessOwner);
            tvAddress = itemView.findViewById(R.id.tvBusinessAddress);
            tvDescription = itemView.findViewById(R.id.tvBusinessDescription);
            btnCall = itemView.findViewById(R.id.btnCallBusiness);
        }
    }
}
