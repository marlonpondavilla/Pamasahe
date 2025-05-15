package com.example.pamasahe.navigation_fragments.fare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pamasahe.R;
import com.example.pamasahe.classes.RideConfirmation;
import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private List<RideConfirmation> historyList;

    public RideAdapter(List<RideConfirmation> historyList) {
        this.historyList = historyList;
    }

    @Override
    public RideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride, parent, false);
        return new RideViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RideViewHolder holder, int position) {
        RideConfirmation ride = historyList.get(position);

        holder.fromText.setText(ride.getFrom());
        holder.toText.setText(ride.getTo());
        holder.dateText.setText(ride.getDateTime());  // Make sure you have a dateTime field
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class RideViewHolder extends RecyclerView.ViewHolder {
        public TextView fromText, toText, dateText;

        public RideViewHolder(View view) {
            super(view);
            fromText = view.findViewById(R.id.fromText);
            toText = view.findViewById(R.id.toText);
            dateText = view.findViewById(R.id.dateText);
        }
    }
}
