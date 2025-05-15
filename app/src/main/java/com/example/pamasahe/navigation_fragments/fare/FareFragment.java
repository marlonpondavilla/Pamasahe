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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
                showPayDialog(ride);
            }

            @Override
            public void onDeleteClick(RideConfirmation ride) {
                deleteRide(ride);
            }
        });

        // Fetch data from Firebase
        fetchConfirmedRides();

        return rootView;
    }

    private void fetchConfirmedRides() {
        loadingText.setVisibility(View.VISIBLE);
        noRidesText.setVisibility(View.GONE);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId != null) {
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("confirmRides").child(userId);

            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        rideConfirmationList.clear();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            RideConfirmation ride = snapshot.getValue(RideConfirmation.class);
                            if (ride != null) {
                                rideConfirmationList.add(ride);  // Add rides with dateTime
                            }
                        }

                        rideConfirmationAdapter.notifyDataSetChanged();
                    } else {
                        noRidesText.setVisibility(View.VISIBLE);
                    }

                    loadingText.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    loadingText.setVisibility(View.GONE);
                }
            });
        }
    }


    private void deleteRide(RideConfirmation ride) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("confirmRides").child(userId);
            ref.orderByChild("from").equalTo(ride.getFrom())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                RideConfirmation r = child.getValue(RideConfirmation.class);
                                if (r != null && r.getTo().equals(ride.getTo()) &&
                                        r.getDuration().equals(ride.getDuration()) &&
                                        r.getFinalPrice().equals(ride.getFinalPrice())) {
                                    child.getRef().removeValue();
                                    Toast.makeText(getContext(), "Ride deleted", Toast.LENGTH_SHORT).show();
                                    // Directly update the list and notify adapter
                                    rideConfirmationList.remove(ride);  // Remove from list
                                    rideConfirmationAdapter.notifyDataSetChanged();  // Notify adapter

                                    // Check if the list is empty and show the "No Rides" text
                                    if (rideConfirmationList.isEmpty()) {
                                        noRidesText.setVisibility(View.VISIBLE);
                                    }

                                    // You can now skip fetching rides again as the adapter has been updated.
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

        arriveButton.setOnClickListener(v -> {
            addRideToDatabaseWithUniqueId(ride);
            dialog.dismiss();
            deleteRide(ride);
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void addRideToDatabaseWithUniqueId(RideConfirmation ride) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId != null) {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference("arrivedRides");
            DatabaseReference newRideRef = database.child(userId).push();

            // Get current time and format it
            String dateTime = getFormattedDateTime();

            // Set the dateTime field in the RideConfirmation object
            ride.setDateTime(dateTime);

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

    private String getFormattedDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy, h:mm a");
        return sdf.format(new Date());
    }
}
