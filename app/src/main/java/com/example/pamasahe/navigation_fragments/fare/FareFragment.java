package com.example.pamasahe.navigation_fragments.fare;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
    private TextView loadingText, noRidesText;

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
        noRidesText = rootView.findViewById(R.id.noConfirmedRidesText);

        rideConfirmationList = new ArrayList<>();
        rideConfirmationAdapter = new RideConfirmationAdapter(rideConfirmationList);
        rideRecyclerView.setAdapter(rideConfirmationAdapter);

        rideConfirmationAdapter.setOnItemActionListener(new RideConfirmationAdapter.OnItemActionListener() {
            @Override
            public void onPayClick(RideConfirmation ride) {
                // 👇 Call showPayDialog here
                showPayDialog(ride);
            }

            @Override
            public void onDeleteClick(RideConfirmation ride) {
                // 👇 Call deleteRide here
                deleteRide(ride);
            }
        });


        // Fetch data from Firebase
        fetchConfirmedRides();

        return rootView;
    }

    private void fetchConfirmedRides() {
        // Show loading text
        loadingText.setVisibility(View.VISIBLE);
        noRidesText.setVisibility(View.GONE);

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
                        noRidesText.setVisibility(View.VISIBLE);
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

    private void deleteRide(RideConfirmation ride) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("confirmRides")
                    .child(userId);

            ref.orderByChild("from").equalTo(ride.getFrom())  // Base filter
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                RideConfirmation r = child.getValue(RideConfirmation.class);

                                if (r != null &&
                                        r.getTo().equals(ride.getTo()) &&
                                        r.getDuration().equals(ride.getDuration()) &&
                                        r.getFinalPrice().equals(ride.getFinalPrice())) {

                                    child.getRef().removeValue();
                                    Toast.makeText(getContext(), "Ride deleted", Toast.LENGTH_SHORT).show();
                                    fetchConfirmedRides();
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Failed to delete ride", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showPayDialog(RideConfirmation ride) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_pay_confirmation, null);
        builder.setView(dialogView);

        TextView fromToText = dialogView.findViewById(R.id.fromToText);

        Button arriveButton = dialogView.findViewById(R.id.arriveButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        fromToText.setText(ride.getFrom() + " → " + ride.getTo());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle "Arrive" button click
        arriveButton.setOnClickListener(v -> {
            // Handle the "Arrive" button action and add to the database under a unique ID
            addRideToDatabaseWithUniqueId(ride);
            dialog.dismiss();
            deleteRide(ride);
        });

        // Handle "Cancel" button click
        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void addRideToDatabaseWithUniqueId(RideConfirmation ride) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId != null) {
            // Reference to the RDB
            DatabaseReference database = FirebaseDatabase.getInstance().getReference("arrivedRides");

            // Use push() to create a unique ID for the new ride
            DatabaseReference newRideRef = database.child(userId).push();

            // Save the ride data under the generated unique ID
            newRideRef.setValue(ride)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "You arrived safely. Have a nice day ahead!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to add ride. Try again!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "User not authenticated. Please sign in first.", Toast.LENGTH_SHORT).show();
        }
    }


}
