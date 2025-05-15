package com.example.pamasahe.navigation_fragments.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pamasahe.R;
import com.example.pamasahe.classes.RideConfirmation;
import com.example.pamasahe.classes.Vehicle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView vehicleRecyclerView;
    private VehicleAdapter vehicleAdapter;
    private List<Vehicle> vehicleList;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        vehicleRecyclerView = rootView.findViewById(R.id.vehicleRecyclerView);
        vehicleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        vehicleList = new ArrayList<>();
        vehicleList.add(new Vehicle("Bambang", "Bulakan", "24 min", "₱45", R.drawable.jeepney_front));
        vehicleList.add(new Vehicle("Perez", "Sta ana", "12 min", "₱40", R.drawable.jeepney_front));
        vehicleList.add(new Vehicle("Balubad", "Tibig", "30 min", "₱50", R.drawable.jeepney_front));
        vehicleList.add(new Vehicle("San Nicolas", "Pitpitan", "20 min", "₱45", R.drawable.jeepney_front));
        vehicleList.add(new Vehicle("Tabang", "Balagtas", "43 min", "₱45", R.drawable.jeepney_front));
        vehicleList.add(new Vehicle("Guiguinto", "Triple", "35 min", "₱50", R.drawable.jeepney_front));

        vehicleAdapter = new VehicleAdapter(vehicleList);
        vehicleRecyclerView.setAdapter(vehicleAdapter);

        vehicleAdapter.setOnVehicleButtonClickListener(vehicle -> showRideConfirmationDialog(vehicle));

        return rootView;
    }

    private void showRideConfirmationDialog(Vehicle vehicle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_ride_confirmation, null);
        builder.setView(dialogView);

        TextView fromToText = dialogView.findViewById(R.id.fromToText);
        TextView durationText = dialogView.findViewById(R.id.durationText);
        TextView originalPriceText = dialogView.findViewById(R.id.originalPriceText);
        TextView finalPriceText = dialogView.findViewById(R.id.finalPriceText);
        Spinner discountSpinner = dialogView.findViewById(R.id.discountSpinner);
        Button confirmRideBtn = dialogView.findViewById(R.id.confirmRideBtn);

        String fromTo = vehicle.getFrom() + " → " + vehicle.getTo();
        fromToText.setText(fromTo);
        durationText.setText("Duration: " + vehicle.getDuration());
        originalPriceText.setText("Original Price: " + vehicle.getPrice());
        finalPriceText.setText("Final Price: " + vehicle.getPrice());

        String[] discounts = {"None", "Student (20%)", "Senior (20%)", "PWD (25%)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, discounts);
        discountSpinner.setAdapter(adapter);

        String priceStr = vehicle.getPrice().replace("₱", "").trim();
        double originalPrice = Double.parseDouble(priceStr);

        discountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                double finalPrice = originalPrice;

                switch (position) {
                    case 1: // Student
                    case 2: // Senior
                        finalPrice = originalPrice * 0.80; // 20% discount
                        break;
                    case 3: // PWD
                        finalPrice = originalPrice * 0.75; // 25% discount
                        break;
                    case 0: // None
                    default:
                        finalPrice = originalPrice;
                        break;
                }

                finalPriceText.setText("Final Price: ₱" + String.format("%.2f", finalPrice));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        confirmRideBtn.setOnClickListener(v -> {
            String from = vehicle.getFrom();
            String to = vehicle.getTo();
            String duration = vehicle.getDuration();
            String originalPriceConf = vehicle.getPrice();
            String finalPrice = finalPriceText.getText().toString();
            String discountType = discountSpinner.getSelectedItem().toString();

            FirebaseAuth auth = FirebaseAuth.getInstance();
            String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

            if (userId != null) {
                // Prepare the ride data
                RideConfirmation ride = new RideConfirmation(from, to, duration, originalPriceConf, finalPrice, discountType);

                // Reference to the RDB
                DatabaseReference database = FirebaseDatabase.getInstance().getReference("confirmRides");

                // Save ride data under the user's unique ID
                DatabaseReference rideRef = database.child(userId).push();
                String rideId = rideRef.getKey();
                ride.setId(rideId);

                rideRef.setValue(ride)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Ride added!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(getContext(), "Failed to confirm ride. Try again!", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getContext(), "User not authenticated. Please sign in first.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
