package com.example.pamasahe.navigation_fragments.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pamasahe.Kilometer;
import com.example.pamasahe.Login;
import com.example.pamasahe.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.net.URL;

public class AccountFragment extends Fragment {

    private Button logoutButton, kilometerButton;
    private ImageView profileImage;
    private TextView userNameText;

    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        auth = FirebaseAuth.getInstance();

        // Initialize Google Sign-In client
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // UI elements
        profileImage = view.findViewById(R.id.profileImage);
        userNameText = view.findViewById(R.id.userNameText);
        logoutButton = view.findViewById(R.id.logoutButton);
        kilometerButton = view.findViewById(R.id.kilometerButton);

        logoutButton.setOnClickListener(v -> performLogout());

        kilometerButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), Kilometer.class);
            startActivity(intent);
        });

        displayUserInfo();

        return view;
    }

    private void displayUserInfo() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // Display name
            String displayName = user.getDisplayName();
            userNameText.setText(displayName != null ? displayName : "Guest");

            // Load profile picture or show default
            Uri photoUrl = user.getPhotoUrl();
            if (photoUrl != null) {
                new Thread(() -> {
                    try {
                        InputStream input = new URL(photoUrl.toString()).openStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(input);
                        requireActivity().runOnUiThread(() -> profileImage.setImageBitmap(bitmap));
                    } catch (Exception e) {
                        requireActivity().runOnUiThread(() -> profileImage.setImageResource(R.drawable.default_profile));
                    }
                }).start();
            } else {
                profileImage.setImageResource(R.drawable.default_profile);
            }

        }
    }

    private void performLogout() {
        auth.signOut();
        googleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireActivity(), Login.class));
                requireActivity().finish();
            } else {
                Toast.makeText(requireActivity(), "Logout failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
