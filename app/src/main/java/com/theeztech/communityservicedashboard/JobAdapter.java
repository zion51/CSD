package com.theeztech.communityservicedashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ViewHolder> {

    private List<Job> jobs;
    private OnJobActionListener listener;

    public interface OnJobActionListener {
        void onDelete(Job job);
    }

    public JobAdapter(List<Job> jobs, OnJobActionListener listener) {
        this.jobs = jobs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Job job = jobs.get(position);
        holder.tvTitle.setText(job.getTitle());
        holder.tvCategory.setText(job.getCategory());
        holder.tvType.setText("Type: " + job.getType());
        holder.tvDeadline.setText("Deadline: " + job.getDeadline());
        holder.tvSalary.setText("Salary: " + job.getSalary());

        holder.btnDelete.setOnClickListener(v -> listener.onDelete(job));
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvType, tvDeadline, tvSalary;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvJobTitle);
            tvCategory = itemView.findViewById(R.id.tvJobCategory);
            tvType = itemView.findViewById(R.id.tvJobType);
            tvDeadline = itemView.findViewById(R.id.tvJobDeadline);
            tvSalary = itemView.findViewById(R.id.tvJobSalary);
            btnDelete = itemView.findViewById(R.id.btnDeleteJob);
        }
    }
}
