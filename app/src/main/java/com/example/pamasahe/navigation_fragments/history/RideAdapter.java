package com.example.pamasahe.navigation_fragments.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pamasahe.R;
import com.example.pamasahe.classes.RideConfirmation;

import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private List<RideConfirmation> rideList;

    public RideAdapter(List<RideConfirmation> rideList) {
        this.rideList = rideList;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        RideConfirmation ride = rideList.get(position);
        holder.from.setText("From: " + ride.getFrom());
        holder.to.setText("To: " + ride.getTo());
        holder.price.setText(ride.getFinalPrice());
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView from, to, price;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            from = itemView.findViewById(R.id.fromText);
            to = itemView.findViewById(R.id.toText);
            price = itemView.findViewById(R.id.priceText);
        }
    }
}
