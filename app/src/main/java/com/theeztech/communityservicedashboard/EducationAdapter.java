package com.theeztech.communityservicedashboard;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.ViewHolder> {

    private Context context;
    private List<EducationInstitution> eduList;

    public EducationAdapter(Context context, List<EducationInstitution> eduList) {
        this.context = context;
        this.eduList = eduList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_education, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EducationInstitution edu = eduList.get(position);
        holder.tvName.setText(edu.getName());
        holder.tvCategory.setText(edu.getCategory());
        holder.tvAddress.setText("Address: " + edu.getAddress());
        
        if (edu.getEiin() != null && !edu.getEiin().isEmpty()) {
            holder.tvEiin.setVisibility(View.VISIBLE);
            holder.tvEiin.setText("EIIN: " + edu.getEiin());
        } else {
            holder.tvEiin.setVisibility(View.GONE);
        }

        if (edu.getWebsite() != null && !edu.getWebsite().isEmpty()) {
            holder.btnWebsite.setVisibility(View.VISIBLE);
        } else {
            holder.btnWebsite.setVisibility(View.GONE);
        }

        holder.btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + edu.getContact()));
            context.startActivity(intent);
        });

        holder.btnWebsite.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(edu.getWebsite()));
            context.startActivity(intent);
        });
        
        // Update icon based on category
        if (edu.getCategory().equalsIgnoreCase("Tutor")) {
            holder.ivIcon.setImageResource(android.R.drawable.ic_menu_myplaces);
            holder.tvEiin.setVisibility(View.GONE);
        } else {
            holder.ivIcon.setImageResource(android.R.drawable.ic_menu_edit);
        }
    }

    @Override
    public int getItemCount() {
        return eduList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvEiin, tvAddress;
        MaterialButton btnCall, btnWebsite;
        ImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEduName);
            tvCategory = itemView.findViewById(R.id.tvEduCategory);
            tvEiin = itemView.findViewById(R.id.tvEduEiin);
            tvAddress = itemView.findViewById(R.id.tvEduAddress);
            btnCall = itemView.findViewById(R.id.btnCallEdu);
            btnWebsite = itemView.findViewById(R.id.btnVisitWebsite);
            ivIcon = itemView.findViewById(R.id.ivEduIcon);
        }
    }
}
