package com.example.pamasahe.navigation_fragments.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pamasahe.Login;
import com.example.pamasahe.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {

    private Button logoutButton;
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Firebase Auth instance
        auth = FirebaseAuth.getInstance();

        // Initialize Google Sign-In Client (if used)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // Setup logout button
        logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> performLogout());

        return view;
    }

    private void performLogout() {
        auth.signOut();
        googleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireActivity(), Login.class);
                startActivity(intent);
                requireActivity().finish();
            } else {
                Toast.makeText(requireActivity(), "Logout failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
