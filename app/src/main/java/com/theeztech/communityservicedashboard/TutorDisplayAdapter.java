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

public class TutorDisplayAdapter extends RecyclerView.Adapter<TutorDisplayAdapter.ViewHolder> {

    private Context context;
    private List<Tutor> tutorList;

    public TutorDisplayAdapter(Context context, List<Tutor> tutorList) {
        this.context = context;
        this.tutorList = tutorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tutor_display, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tutor tutor = tutorList.get(position);
        holder.tvName.setText(tutor.getName());
        holder.tvEdu.setText(tutor.getEducation());
        holder.tvSubjects.setText("Subjects: " + tutor.getSubjects());
        holder.tvSalary.setText("Salary: " + tutor.getSalary());

        holder.btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + tutor.getPhone()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tutorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEdu, tvSubjects, tvSalary;
        MaterialButton btnCall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTutorName);
            tvEdu = itemView.findViewById(R.id.tvTutorEdu);
            tvSubjects = itemView.findViewById(R.id.tvTutorSubjects);
            tvSalary = itemView.findViewById(R.id.tvTutorSalary);
            btnCall = itemView.findViewById(R.id.btnCallTutor);
        }
    }
}
