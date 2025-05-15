package com.example.pamasahe.navigation_fragments.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pamasahe.R;
import com.example.pamasahe.classes.RideConfirmation;
import com.example.pamasahe.navigation_fragments.fare.RideAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView historyRecyclerView;
    private RideAdapter historyAdapter;
    private List<RideConfirmation> historyList;
    private TextView loadingText, noRidesText;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        historyRecyclerView = rootView.findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadingText = rootView.findViewById(R.id.loadingText);
        noRidesText = rootView.findViewById(R.id.noConfirmedRidesText);

        historyList = new ArrayList<>();
        historyAdapter = new RideAdapter(historyList);
        historyRecyclerView.setAdapter(historyAdapter);

        // Fetch data from Firebase
        fetchHistoryRides();

        return rootView;
    }

    private void fetchHistoryRides() {
        loadingText.setVisibility(View.VISIBLE);
        noRidesText.setVisibility(View.GONE);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId != null) {
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("arrivedRides").child(userId);

            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        historyList.clear();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            RideConfirmation ride = snapshot.getValue(RideConfirmation.class);
                            if (ride != null) {
                                historyList.add(ride);
                            }
                        }

                        // Notify the adapter that data has changed
                        historyAdapter.notifyDataSetChanged();

                        // Hide loading text and show history
                        loadingText.setVisibility(View.GONE);

                        // If no rides were fetched, show the "No Rides" message
                        if (historyList.isEmpty()) {
                            noRidesText.setVisibility(View.VISIBLE);
                        } else {
                            noRidesText.setVisibility(View.GONE);
                        }

                    } else {
                        // No data found, show "No Rides" message
                        loadingText.setVisibility(View.GONE);
                        noRidesText.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    loadingText.setVisibility(View.GONE);
                    noRidesText.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
