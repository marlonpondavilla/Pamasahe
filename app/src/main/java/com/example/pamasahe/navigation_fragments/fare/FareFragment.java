package com.example.pamasahe.navigation_fragments.fare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pamasahe.R;
import com.example.pamasahe.classes.RideConfirmation;
import com.example.pamasahe.navigation_fragments.fare.RideConfirmationAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FareFragment extends Fragment {

    private RecyclerView rideRecyclerView;
    private RideConfirmationAdapter rideConfirmationAdapter;
    private List<RideConfirmation> rideConfirmationList;
    private TextView loadingText;

    public FareFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fare, container, false);

        rideRecyclerView = rootView.findViewById(R.id.rideRecyclerView);
        rideRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadingText = rootView.findViewById(R.id.loadingText);

        rideConfirmationList = new ArrayList<>();
        rideConfirmationAdapter = new RideConfirmationAdapter(rideConfirmationList);
        rideRecyclerView.setAdapter(rideConfirmationAdapter);

        // Fetch data from Firebase
        fetchConfirmedRides();

        return rootView;
    }

    private void fetchConfirmedRides() {
        // Show loading text
        loadingText.setVisibility(View.VISIBLE);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId != null) {
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("confirmRides").child(userId);

            // Fetch data using addListenerForSingleValueEvent
            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        rideConfirmationList.clear(); // Clear the list before adding new data

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            RideConfirmation ride = snapshot.getValue(RideConfirmation.class);
                            if (ride != null) {
                                rideConfirmationList.add(ride);
                            }
                        }

                        // Notify the adapter that data has changed
                        rideConfirmationAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "No confirmed rides available", Toast.LENGTH_SHORT).show();
                    }

                    // Hide the loading text once the data is fetched
                    loadingText.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                    Toast.makeText(getContext(), "Failed to load confirmed rides", Toast.LENGTH_SHORT).show();

                    // Hide the loading text if data fetching fails
                    loadingText.setVisibility(View.GONE);
                }
            });
        }
    }
}
