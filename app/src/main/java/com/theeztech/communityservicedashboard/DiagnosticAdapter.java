package com.theeztech.communityservicedashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DiagnosticAdapter extends RecyclerView.Adapter<DiagnosticAdapter.ViewHolder> {

    private List<DiagnosticTest> tests;
    private OnDiagnosticActionListener listener;

    public interface OnDiagnosticActionListener {
        void onEdit(DiagnosticTest test);
        void onDelete(DiagnosticTest test);
    }

    public DiagnosticAdapter(List<DiagnosticTest> tests, OnDiagnosticActionListener listener) {
        this.tests = tests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false); // Reusing item_service
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiagnosticTest test = tests.get(position);
        holder.tvName.setText(test.getName());
        holder.tvDesc.setText(test.getDescription());
        holder.tvPrice.setText("Price: " + test.getPrice());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(test));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(test));
    }

    @Override
    public int getItemCount() {
        return tests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvPrice;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvServiceName);
            tvDesc = itemView.findViewById(R.id.tvServiceDesc);
            tvPrice = itemView.findViewById(R.id.tvServicePrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
