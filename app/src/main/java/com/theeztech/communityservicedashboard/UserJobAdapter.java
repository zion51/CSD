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

public class UserJobAdapter extends RecyclerView.Adapter<UserJobAdapter.ViewHolder> {

    private Context context;
    private List<Job> jobList;

    public UserJobAdapter(Context context, List<Job> jobList) {
        this.context = context;
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.tvTitle.setText(job.getTitle());
        holder.tvCategory.setText(job.getCategory());
        holder.tvType.setText("Type: " + job.getType());
        holder.tvDeadline.setText("Deadline: " + job.getDeadline());
        holder.tvWorkplace.setText("Workplace: " + job.getWorkplace());
        holder.tvSalary.setText("Salary: " + job.getSalary());
        holder.tvDescription.setText(job.getDescription());

        holder.btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + job.getOwnerPhone()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvType, tvDeadline, tvWorkplace, tvSalary, tvDescription;
        MaterialButton btnCall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvJobTitle);
            tvCategory = itemView.findViewById(R.id.tvJobCategory);
            tvType = itemView.findViewById(R.id.tvJobType);
            tvDeadline = itemView.findViewById(R.id.tvJobDeadline);
            tvWorkplace = itemView.findViewById(R.id.tvJobWorkplace);
            tvSalary = itemView.findViewById(R.id.tvJobSalary);
            tvDescription = itemView.findViewById(R.id.tvJobDescription);
            btnCall = itemView.findViewById(R.id.btnCallEmployer);
        }
    }
}
