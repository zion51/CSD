package com.theeztech.communityservicedashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> {

    private List<Doctor> doctors;
    private OnDoctorActionListener listener;
    private OnDoctorClickListener clickListener;
    private boolean isViewOnly = false;
    private boolean showPendingCount = false;
    private boolean showBookButton = false;

    public interface OnDoctorActionListener {
        void onEdit(Doctor doctor);
        void onDelete(Doctor doctor);
    }

    public interface OnDoctorClickListener {
        void onDoctorClick(Doctor doctor);
    }

    public DoctorAdapter(List<Doctor> doctors, OnDoctorActionListener listener) {
        this.doctors = doctors;
        this.listener = listener;
        this.isViewOnly = false;
    }

    public DoctorAdapter(List<Doctor> doctors, OnDoctorClickListener clickListener) {
        this.doctors = doctors;
        this.clickListener = clickListener;
        this.isViewOnly = true;
    }

    public void setShowPendingCount(boolean showPendingCount) {
        this.showPendingCount = showPendingCount;
    }

    public void setShowBookButton(boolean showBookButton) {
        this.showBookButton = showBookButton;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        holder.tvName.setText(doctor.getName());
        holder.tvSpecialty.setText(doctor.getSpecialization());
        holder.tvQualification.setText(doctor.getQualification());
        holder.tvHours.setText("Time: " + doctor.getChamberTime());
        holder.tvDays.setText("Days: " + doctor.getChamberDay());
        holder.tvFee.setText("Fee: " + doctor.getVisitFee());
        holder.tvPhone.setText("Phone: " + doctor.getPhone());

        String hcName = doctor.getHealthcareName();
        if (hcName != null && !hcName.isEmpty() && !hcName.equals("null")) {
            holder.tvHealthcareName.setVisibility(View.VISIBLE);
            holder.tvHealthcareName.setText("At: " + hcName);
        } else {
            holder.tvHealthcareName.setVisibility(View.GONE);
        }

        if (showPendingCount) {
            holder.tvPendingCount.setVisibility(View.VISIBLE);
            holder.tvPendingCount.setText(doctor.getPendingCount() + " Pending");
        } else {
            holder.tvPendingCount.setVisibility(View.GONE);
        }

        if (isViewOnly) {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
            
            if (showBookButton) {
                holder.btnBookSerial.setVisibility(View.VISIBLE);
                holder.btnBookSerial.setOnClickListener(v -> {
                    if (clickListener != null) clickListener.onDoctorClick(doctor);
                });
            } else {
                holder.btnBookSerial.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onDoctorClick(doctor);
            });
        } else {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnBookSerial.setVisibility(View.GONE);
            
            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(doctor);
            });
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(doctor);
            });
        }
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSpecialty, tvHealthcareName, tvQualification, tvHours, tvDays, tvFee, tvPhone, tvPendingCount;
        ImageButton btnEdit, btnDelete;
        View btnBookSerial;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvDoctorName);
            tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
            tvHealthcareName = itemView.findViewById(R.id.tvHealthcareName);
            tvQualification = itemView.findViewById(R.id.tvQualification);
            tvHours = itemView.findViewById(R.id.tvVisitingHours);
            tvDays = itemView.findViewById(R.id.tvVisitingDays);
            tvFee = itemView.findViewById(R.id.tvFee);
            tvPhone = itemView.findViewById(R.id.tvDoctorPhone);
            tvPendingCount = itemView.findViewById(R.id.tvPendingCount);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnBookSerial = itemView.findViewById(R.id.btnBookSerial);
        }
    }
}
