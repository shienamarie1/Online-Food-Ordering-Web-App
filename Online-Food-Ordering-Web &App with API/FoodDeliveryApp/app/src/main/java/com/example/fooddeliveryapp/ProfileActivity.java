package com.example.fooddeliveryapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import Model.User;
import Network.ApiService;
import Network.RetrofitClient;
import Network.WalletResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    LinearLayout navHome, navOrders, navDelivery, navProfile;
    TextView txtName, txtEmail, txtPhone, txtWallet;
    Button btnLogout;

    int riderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // TEXTVIEWS
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        txtWallet = findViewById(R.id.txtWallet);

        // BUTTON
        btnLogout = findViewById(R.id.btnLogout);

        // NAVIGATION
        navHome = findViewById(R.id.navHome);
        navOrders = findViewById(R.id.navOrders);
        navDelivery = findViewById(R.id.navDelivery);
        navProfile = findViewById(R.id.navProfile);

        // GET RIDER ID FROM LOGIN
        riderId = getIntent().getIntExtra("user_id", 1);

        loadProfile();

        // LOGOUT
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });



        // PROFILE (refresh only)
        FloatingHelper.setup(this,
                riderId,
                navHome,
                navOrders,
                navDelivery,
                navProfile
        );
    }

    private void loadProfile() {

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.getUserProfile(String.valueOf(riderId))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            User user = response.body();

                            txtName.setText(user.getName());

                            txtEmail.setText(
                                    user.getEmail() != null &&
                                            !user.getEmail().isEmpty()
                                            ? user.getEmail()
                                            : "No Email"
                            );

                            txtPhone.setText(
                                    user.getContact() != null &&
                                            !user.getContact().isEmpty()
                                            ? user.getContact()
                                            : "No Contact"
                            );

                            loadWallet();

                        } else {
                            Toast.makeText(ProfileActivity.this,
                                    "Profile not found",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                        Log.e("PROFILE_ERROR", t.getMessage());

                        Toast.makeText(ProfileActivity.this,
                                "Failed: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void loadWallet() {

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.getWallet(String.valueOf(riderId))
                .enqueue(new Callback<WalletResponse>() {
                    @Override
                    public void onResponse(Call<WalletResponse> call, Response<WalletResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            txtWallet.setText("₱" + response.body().getBalance());

                        } else {
                            txtWallet.setText("₱0.00");
                        }
                    }

                    @Override
                    public void onFailure(Call<WalletResponse> call, Throwable t) {

                        txtWallet.setText("₱0.00");
                        Log.e("WALLET_ERROR", t.getMessage());
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }
}