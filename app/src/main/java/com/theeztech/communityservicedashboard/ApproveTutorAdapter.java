package com.theeztech.communityservicedashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ApproveTutorAdapter extends RecyclerView.Adapter<ApproveTutorAdapter.ViewHolder> {

    private List<Tutor> tutorList;
    private OnTutorApprovalListener listener;

    public interface OnTutorApprovalListener {
        void onApprove(Tutor tutor);
        void onReject(Tutor tutor);
    }

    public ApproveTutorAdapter(List<Tutor> tutorList, OnTutorApprovalListener listener) {
        this.tutorList = tutorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_approve_tutor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tutor tutor = tutorList.get(position);
        holder.tvName.setText(tutor.getName());
        holder.tvEdu.setText(tutor.getEducation());
        holder.tvSubjects.setText("Subjects: " + tutor.getSubjects());
        holder.tvSalary.setText("Expected: " + tutor.getSalary());
        holder.tvAddress.setText("Area: " + tutor.getAddress());
        holder.tvExp.setText(tutor.getExperience());

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(tutor));
        holder.btnReject.setOnClickListener(v -> listener.onReject(tutor));
    }

    @Override
    public int getItemCount() {
        return tutorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEdu, tvSubjects, tvSalary, tvAddress, tvExp;
        MaterialButton btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTutorName);
            tvEdu = itemView.findViewById(R.id.tvTutorEdu);
            tvSubjects = itemView.findViewById(R.id.tvTutorSubjects);
            tvSalary = itemView.findViewById(R.id.tvTutorSalary);
            tvAddress = itemView.findViewById(R.id.tvTutorAddress);
            tvExp = itemView.findViewById(R.id.tvTutorExp);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
