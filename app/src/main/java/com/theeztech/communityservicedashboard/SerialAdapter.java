package com.theeztech.communityservicedashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SerialAdapter extends RecyclerView.Adapter<SerialAdapter.ViewHolder> {

    private List<DoctorSerial> serials;
    private OnSerialActionListener listener;

    public interface OnSerialActionListener {
        void onUpdateStatus(DoctorSerial serial);
    }

    public SerialAdapter(List<DoctorSerial> serials, OnSerialActionListener listener) {
        this.serials = serials;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_serial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DoctorSerial serial = serials.get(position);
        holder.tvSerialNumber.setText(serial.getSerialNumber());
        holder.tvPatientName.setText(serial.getPatientName());
        holder.tvDoctorName.setText("Doctor: " + serial.getDoctorName());
        holder.tvPatientPhone.setText("Phone: " + serial.getPatientPhone());
        
        String status = serial.getStatus();
        holder.tvStatus.setText(status);

        updateStatusStyle(holder.tvStatus, status);

        holder.btnUpdate.setOnClickListener(v -> listener.onUpdateStatus(serial));
    }

    private void updateStatusStyle(TextView tvStatus, String status) {
        if (status == null) return;
        
        String s = status.toLowerCase().trim();
        switch (s) {
            case "complete":
            case "completed":
                tvStatus.setText("Completed");
                tvStatus.setBackgroundResource(R.drawable.bg_status_completed);
                break;
            case "cancelled":
            case "cancel":
                tvStatus.setText("Cancelled");
                tvStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
                break;
            default:
                tvStatus.setText("Pending");
                tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return serials.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSerialNumber, tvPatientName, tvDoctorName, tvPatientPhone, tvStatus;
        MaterialButton btnUpdate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSerialNumber = itemView.findViewById(R.id.tvSerialNumber);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvPatientPhone = itemView.findViewById(R.id.tvPatientPhone);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnUpdate = itemView.findViewById(R.id.btnUpdateStatus);
        }
    }
}
