package com.example.pamasahe.navigation_fragments.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pamasahe.R;
import com.example.pamasahe.classes.Vehicle;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private List<Vehicle> vehicleList;

    public VehicleAdapter(List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_card_template, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);

        holder.fromText.setText(vehicle.getFrom());
        holder.toText.setText(vehicle.getTo());
        holder.durationText.setText(vehicle.getDuration());
        holder.priceText.setText(vehicle.getPrice());
        holder.vehicleImage.setImageResource(vehicle.getImageResId());
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public static class VehicleViewHolder extends RecyclerView.ViewHolder {
        TextView fromText, toText, durationText, priceText;
        ImageView vehicleImage;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            fromText = itemView.findViewById(R.id.fromText);
            toText = itemView.findViewById(R.id.toText);
            durationText = itemView.findViewById(R.id.durationText);
            priceText = itemView.findViewById(R.id.priceText);
            vehicleImage = itemView.findViewById(R.id.vehicleImage);
        }
    }
}
