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

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.ViewHolder> {

    private Context context;
    private List<Donor> donorList;

    public DonorAdapter(Context context, List<Donor> donorList) {
        this.context = context;
        this.donorList = donorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_donor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Donor donor = donorList.get(position);
        holder.tvDonorName.setText(donor.getName());
        holder.tvDonorGroup.setText("Blood Group: " + donor.getBloodGroup());
        holder.tvLastDonation.setText("Last Donation: " + donor.getLastDonationDate());

        holder.btnCallDonor.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + donor.getPhone()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return donorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDonorName, tvDonorGroup, tvLastDonation;
        MaterialButton btnCallDonor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDonorName = itemView.findViewById(R.id.tvDonorName);
            tvDonorGroup = itemView.findViewById(R.id.tvDonorGroup);
            tvLastDonation = itemView.findViewById(R.id.tvLastDonation);
            btnCallDonor = itemView.findViewById(R.id.btnCallDonor);
        }
    }
}