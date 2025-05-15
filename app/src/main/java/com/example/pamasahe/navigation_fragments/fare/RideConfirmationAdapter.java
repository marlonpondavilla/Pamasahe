package com.example.pamasahe.navigation_fragments.fare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pamasahe.R;
import com.example.pamasahe.classes.RideConfirmation;

import java.util.List;

public class RideConfirmationAdapter extends RecyclerView.Adapter<RideConfirmationAdapter.ViewHolder> {

    private List<RideConfirmation> rideList;

    // Constructor to initialize the ride list
    public RideConfirmationAdapter(List<RideConfirmation> rideList) {
        this.rideList = rideList;
    }

    // ViewHolder class to hold the views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fromToText, durationText, priceText, discountText;

        public ViewHolder(View itemView) {
            super(itemView);
            fromToText = itemView.findViewById(R.id.fromToText);
            durationText = itemView.findViewById(R.id.durationText);
            priceText = itemView.findViewById(R.id.priceText);
            discountText = itemView.findViewById(R.id.discountText);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for the individual items in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride_confirmation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RideConfirmation ride = rideList.get(position);

        // Bind the ride data to the views
        holder.fromToText.setText("From: " + ride.getFrom() + " → To: " + ride.getTo());
        holder.durationText.setText("Duration: " + ride.getDuration());
        holder.priceText.setText("Price: " + ride.getFinalPrice());
        holder.discountText.setText("Discount: " + ride.getDiscountType());
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }
}
