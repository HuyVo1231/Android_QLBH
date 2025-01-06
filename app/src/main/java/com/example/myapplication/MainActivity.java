package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.model.CartManager;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import com.example.myapplication.model.UserManager;

public class MainActivity extends AppCompatActivity {
    ImageView btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogout = findViewById(R.id.buttonLogout);

        // Find the NavController for the fragmentContainerView
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);

        // Find the BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Link BottomNavigationView with NavController
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Handle incoming intent for navigation
        handleIntent(navController);

        // get username and update UI
        UpdateInfoUser();

        // Hidden UI
        // Observe NavController destination changes
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();

            // Find views to toggle visibility
            View bellIcon = findViewById(R.id.imageViewBell);
            View logoutIcon = findViewById(R.id.buttonLogout);
            View welcomeText = findViewById(R.id.textView15);
            View nameText = findViewById(R.id.userName);
            View exploreText = findViewById(R.id.textView10);

            // Check if we are in Profile Fragment
            if (destinationId == R.id.profileFragment) {
                bellIcon.setVisibility(View.GONE);
                logoutIcon.setVisibility(View.GONE);
                welcomeText.setVisibility(View.GONE);
                nameText.setVisibility(View.GONE);
                exploreText.setVisibility(View.GONE);
            } else {
                bellIcon.setVisibility(View.VISIBLE);
                logoutIcon.setVisibility(View.VISIBLE);
                welcomeText.setVisibility(View.VISIBLE);
                nameText.setVisibility(View.VISIBLE);
                exploreText.setVisibility(View.VISIBLE);
            }
        });

        // Handle the logout button click
        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        // Clear user data from UserManager
        UserManager.getInstance().clearUser();
        CartManager.getInstance().clearCart();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void UpdateInfoUser() {
        // Get user data from UserManager and update UI
        UserManager userManager = UserManager.getInstance();
        User currentUser = userManager.getUser();

        if (currentUser != null) {
            TextView nameTextView = findViewById(R.id.userName);
            nameTextView.setText(currentUser.getFullName());
        }
    }

    private void handleIntent(NavController navController) {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("fragment_id")) {
            int fragmentId = intent.getIntExtra("fragment_id", -1);

            // Ensure we're not navigating to the same destination
            if (fragmentId != -1 && navController.getCurrentDestination() != null
                    && navController.getCurrentDestination().getId() != fragmentId) {
                navController.popBackStack();
                navController.navigate(fragmentId);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        // Handle new Intent for navigation
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);
        handleIntent(navController);
    }
}
