package com.example.pamasahe;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Kilometer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kilometer);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Map<String, Double> distances = new HashMap<>();
        distances.put("Bagumbayan-San Jose", 3.5);
        distances.put("Matungao-San Jose", 5.2);
        distances.put("San Jose-Tibagin", 6.0);
        distances.put("Bagumbayan-Matungao", 2.0);
        distances.put("Perez-San Jose", 4.5);
        distances.put("Maysantol-Pitpitan", 3.2);
        distances.put("Binuangan-San Jose", 8.0);

        Spinner spinnerFrom = findViewById(R.id.spinnerFrom);
        Spinner spinnerTo = findViewById(R.id.spinnerTo);
        TextView textViewDistance = findViewById(R.id.textViewDistance);
        TextView textViewPrice = findViewById(R.id.textViewPrice);
        Button buttonCalculate = findViewById(R.id.buttonCalculate);
        TextView buttonBack = findViewById(R.id.buttonBack);

        String[] places = {"San Jose", "Bagumbayan", "Matungao", "Maysantol", "Tibagin", "Perez", "Pitpitan", "Binuangan"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, places);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        buttonCalculate.setOnClickListener(v -> {
            String from = spinnerFrom.getSelectedItem().toString();
            String to = spinnerTo.getSelectedItem().toString();

            if (from.equals(to)) {
                textViewDistance.setText("Distance: 0 km");
                textViewPrice.setText("Estimated Price: ₱0.00");
                return;
            }

            String key = from.compareTo(to) < 0 ? from + "-" + to : to + "-" + from;
            Double distance = distances.get(key);

            if (distance == null) {
                int indexFrom = Arrays.asList(places).indexOf(from);
                int indexTo = Arrays.asList(places).indexOf(to);
                int steps = Math.abs(indexFrom - indexTo);
                distance = 1.8 * steps;
            }

            double pricePerKm = 10.0;
            double totalPrice = distance * pricePerKm;

            textViewDistance.setText("Distance: " + String.format("%.2f", distance) + " km");
            textViewPrice.setText("Estimated Price: ₱" + String.format("%.2f", totalPrice));

            // Show dialog popup
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Calculation Result")
                    .setMessage("Distance: " + String.format("%.2f", distance) + " km\n" +
                            "Estimated Price: ₱" + String.format("%.2f", totalPrice))
                    .setPositiveButton("OK", null)
                    .show();
        });

        buttonBack.setOnClickListener(v -> finish());
    }
}
