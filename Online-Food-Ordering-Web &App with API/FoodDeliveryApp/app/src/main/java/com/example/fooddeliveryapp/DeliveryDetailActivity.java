package com.example.fooddeliveryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import Model.Order;
import Network.ApiService;
import Network.BasicResponse;
import Network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryDetailActivity extends AppCompatActivity {

    LinearLayout navHome, navOrders, navDelivery, navProfile;

    TextView txtCustomerName, txtAddress, txtPhone, txtTotal;

    Button btnNavigate, btnDelivered;

    String orderId = "";
    String address = "";

    int riderId;

    Handler handler = new Handler();
    Runnable refreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delievery_screen);

        // TEXTVIEW
        txtCustomerName = findViewById(R.id.txtCustomerName);
        txtAddress = findViewById(R.id.txtAddress);
        txtPhone = findViewById(R.id.txtPhone);
        txtTotal = findViewById(R.id.txtTotal);

        // BUTTONS
        btnNavigate = findViewById(R.id.btnNavigate);
        btnDelivered = findViewById(R.id.btnDelivered);

        // NAVIGATION
        navHome = findViewById(R.id.navHome);
        navOrders = findViewById(R.id.navOrders);
        navDelivery = findViewById(R.id.navDelivery);
        navProfile = findViewById(R.id.navProfile);

        riderId = getIntent().getIntExtra("user_id", 1);

        FloatingHelper.setup(
                this,
                riderId,
                navHome,
                navOrders,
                navDelivery,
                navProfile
        );

        // FIRST LOAD
        loadOutForDelivery();

        // AUTO REFRESH
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadOutForDelivery();
                handler.postDelayed(this, 3000);
            }
        };

        handler.postDelayed(refreshRunnable, 3000);

        // OPEN MAPS
        btnNavigate.setOnClickListener(v -> {

            if (address.isEmpty()) {
                Toast.makeText(this, "No address found", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri uri = Uri.parse(
                    "https://www.google.com/maps/dir/?api=1&destination="
                            + Uri.encode(address)
            );

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        });

        // DELIVERED
        btnDelivered.setOnClickListener(v -> updateDelivered());
    }

    // LOAD CURRENT DELIVERY
    private void loadOutForDelivery() {

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getRiderOrders(String.valueOf(riderId))
                .enqueue(new Callback<List<Order>>() {
                    @Override
                    public void onResponse(Call<List<Order>> call,
                                           Response<List<Order>> response) {

                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().isEmpty()) {

                            clearScreen();
                            return;
                        }

                        Order activeOrder = null;

                        for (Order order : response.body()) {

                            String status = order.status != null
                                    ? order.status.toLowerCase()
                                    : "";

                            if (status.contains("out")
                                    && status.contains("delivery")) {

                                activeOrder = order;
                                break;
                            }
                        }

                        if (activeOrder == null) {
                            clearScreen();
                            return;
                        }

                        orderId = String.valueOf(activeOrder.id);
                        address = activeOrder.address != null
                                ? activeOrder.address
                                : "";

                        txtCustomerName.setText(activeOrder.customer_name);
                        txtAddress.setText(address);
                        txtPhone.setText(activeOrder.contact);
                        txtTotal.setText("Total: ₱" + activeOrder.total);
                    }

                    @Override
                    public void onFailure(Call<List<Order>> call,
                                          Throwable t) {

                        Toast.makeText(
                                DeliveryDetailActivity.this,
                                "Network Error",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // UPDATE DELIVERED
    private void updateDelivered() {

        if (orderId.isEmpty()) {
            Toast.makeText(this, "No active delivery", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.updateStatus(orderId, "Delivered", String.valueOf(riderId))
                .enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call,
                                           Response<BasicResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            Toast.makeText(
                                    DeliveryDetailActivity.this,
                                    "Order Delivered",
                                    Toast.LENGTH_SHORT
                            ).show();

                            loadOutForDelivery();

                        } else {
                            Toast.makeText(
                                    DeliveryDetailActivity.this,
                                    "Failed to update",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call,
                                          Throwable t) {

                        Toast.makeText(
                                DeliveryDetailActivity.this,
                                "Network Error",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // CLEAR SCREEN
    private void clearScreen() {

        orderId = "";
        address = "";

        txtCustomerName.setText("No Active Delivery");
        txtAddress.setText("-");
        txtPhone.setText("-");
        txtTotal.setText("Total: ₱0.00");
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refreshRunnable);
    }
}