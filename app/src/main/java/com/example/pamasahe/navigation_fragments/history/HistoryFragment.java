package com.example.pamasahe.navigation_fragments.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pamasahe.R;
import com.example.pamasahe.classes.RideConfirmation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private RideAdapter rideAdapter;
    private List<RideConfirmation> rideList = new ArrayList<>();

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rideAdapter = new RideAdapter(rideList);
        recyclerView.setAdapter(rideAdapter);

        fetchRideHistory();

        return view;
    }

    private void fetchRideHistory() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId != null) {
            DatabaseReference database = FirebaseDatabase.getInstance()
                    .getReference("arrivedRides")
                    .child(userId);

            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    rideList.clear();
                    for (DataSnapshot rideSnapshot : snapshot.getChildren()) {
                        RideConfirmation ride = rideSnapshot.getValue(RideConfirmation.class);
                        if (ride != null) {
                            ride.setId(rideSnapshot.getKey());
                            rideList.add(ride);
                        }
                    }
                    rideAdapter.notifyDataSetChanged();
                    Log.d("HistoryFragment", "Fetched " + rideList.size() + " rides");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("HistoryFragment", "Database error: " + error.getMessage());
                }
            });
        }
    }
}
