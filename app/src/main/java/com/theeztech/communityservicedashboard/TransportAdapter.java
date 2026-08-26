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

public class TransportAdapter extends RecyclerView.Adapter<TransportAdapter.ViewHolder> {

    private Context context;
    private List<Transport> transportList;

    public TransportAdapter(Context context, List<Transport> transportList) {
        this.context = context;
        this.transportList = transportList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transport, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transport transport = transportList.get(position);
        holder.tvDriverName.setText(transport.getDriverName());
        holder.tvVehicleInfo.setText(transport.getVehicleType() + " • " + transport.getVehicleNumber());
        holder.tvContact.setText("Contact: " + transport.getContactNumber());

        // Set Icon based on vehicle type
        String icon = "🚐";
        String type = transport.getVehicleType().toLowerCase();
        if (type.contains("ambulance")) icon = "🚑";
        else if (type.contains("bike")) icon = "🏍️";
        else if (type.contains("car")) icon = "🚗";
        else if (type.contains("pickup")) icon = "🛻";
        else if (type.contains("van")) icon = "🚚";
        else if (type.contains("truck")) icon = "🚛";
        
        holder.tvVehicleIcon.setText(icon);

        holder.btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + transport.getContactNumber()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return transportList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDriverName, tvVehicleInfo, tvContact, tvVehicleIcon;
        MaterialButton btnCall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDriverName = itemView.findViewById(R.id.tvDriverName);
            tvVehicleInfo = itemView.findViewById(R.id.tvVehicleInfo);
            tvContact = itemView.findViewById(R.id.tvContact);
            tvVehicleIcon = itemView.findViewById(R.id.tvVehicleIcon);
            btnCall = itemView.findViewById(R.id.btnCall);
        }
    }
}
