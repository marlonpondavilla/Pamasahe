package com.example.pamasahe.navigation_fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pamasahe.R;
import com.example.pamasahe.classes.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView vehicleRecyclerView;
    private VehicleAdapter vehicleAdapter;
    private List<Vehicle> vehicleList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize RecyclerView and Vehicle List
        vehicleRecyclerView = rootView.findViewById(R.id.vehicleRecyclerView);
        vehicleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Vehicle List
        vehicleList = new ArrayList<>();
        vehicleList.add(new Vehicle("Bambang", "Bulakan", "24 min", "₱45", R.drawable.jeepney_front));
        vehicleList.add(new Vehicle("Perez", "Sta ana", "12 min", "₱40", R.drawable.jeepney_front));
        vehicleList.add(new Vehicle("Balubad", "Tibig", "30 min", "₱50", R.drawable.jeepney_front));
        vehicleList.add(new Vehicle("San Nicolas", "Pitpitan", "20 min", "₱45", R.drawable.jeepney_front));
        vehicleList.add(new Vehicle("Tabang", "Balagtas", "43 min", "₱45", R.drawable.jeepney_front));
        vehicleList.add(new Vehicle("Guiguinto", "Triple", "35 min", "₱50", R.drawable.jeepney_front));


        // Set up the Adapter
        vehicleAdapter = new VehicleAdapter(vehicleList);
        vehicleRecyclerView.setAdapter(vehicleAdapter);

        return rootView;
    }
}
