package com.example.pamasahe.navigation_fragments.fare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pamasahe.R;
import com.example.pamasahe.classes.RideConfirmation;

import java.util.List;

public class RideConfirmationAdapter extends RecyclerView.Adapter<RideConfirmationAdapter.ViewHolder> {

    private List<RideConfirmation> rides;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onPayClick(RideConfirmation ride);
        void onDeleteClick(RideConfirmation ride);
    }

    public void setOnItemActionListener(OnItemActionListener listener) {
        this.listener = listener;
    }

    public RideConfirmationAdapter(List<RideConfirmation> rides) {
        this.rides = rides;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fromTo, duration, price, discount;
        Button payBtn;
        ImageButton deleteBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            fromTo = itemView.findViewById(R.id.fromToText);
            duration = itemView.findViewById(R.id.durationText);
            price = itemView.findViewById(R.id.priceText);
            discount = itemView.findViewById(R.id.discountText);
            payBtn = itemView.findViewById(R.id.payBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride_confirmation, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RideConfirmation ride = rides.get(position);

        holder.fromTo.setText("From: " + ride.getFrom() + " → To: " + ride.getTo());
        holder.duration.setText("Duration: " + ride.getDuration());
        holder.price.setText("Price: " + ride.getFinalPrice());
        holder.discount.setText("Discount: " + ride.getDiscountType());

        holder.payBtn.setOnClickListener(v -> {
            if (listener != null) listener.onPayClick(ride);
        });

        holder.deleteBtn.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(ride);
        });
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }
}
