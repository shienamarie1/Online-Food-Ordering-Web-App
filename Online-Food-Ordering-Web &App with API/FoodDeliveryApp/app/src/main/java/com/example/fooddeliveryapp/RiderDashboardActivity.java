package com.example.fooddeliveryapp;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Adapter.OrderAdapter;
import Model.Order;
import Network.ApiService;
import Network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiderDashboardActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    OrderAdapter adapter;

    Button btnReload;

    LinearLayout navHome, navOrders, navDelivery, navProfile;

    TextView txtAssignedCount, txtDeliveredCount;

    int riderId;

    private final Handler handler = new Handler();
    private Runnable refreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_dashboard);

        // INIT UI
        recyclerView = findViewById(R.id.recyclerOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        txtAssignedCount = findViewById(R.id.txtAssignedCount);
        txtDeliveredCount = findViewById(R.id.txtDeliveredCount);

        navHome = findViewById(R.id.navHome);
        navOrders = findViewById(R.id.navOrders);
        navDelivery = findViewById(R.id.navDelivery);
        navProfile = findViewById(R.id.navProfile);



        riderId = getIntent().getIntExtra("user_id", 1);

        // FIRST LOAD
        loadOrders();

        // MANUAL RELOAD BUTTON


        // AUTO REFRESH EVERY 10 SECONDS
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadOrders();
                handler.postDelayed(this, 10000);
            }
        };

        handler.postDelayed(refreshRunnable, 10000);

        // NAVIGATION
        FloatingHelper.setup(this,
                riderId,
                navHome,
                navOrders,
                navDelivery,
                navProfile
        );
    }

    // ===================== LOAD ORDERS =====================
    private void loadOrders() {

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        Log.d("API_DEBUG", "Fetching orders for Rider ID: " + riderId);

        api.getRiderOrders(String.valueOf(riderId))
                .enqueue(new Callback<List<Order>>() {

                    @Override
                    public void onResponse(Call<List<Order>> call,
                                           Response<List<Order>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            List<Order> allOrders = response.body();
                            List<Order> displayList = new ArrayList<>();

                            int assignedCount = 0;
                            int deliveredCount = 0;

                            for (Order o : allOrders) {

                                String status = o.status != null
                                        ? o.status.toLowerCase()
                                        : "";

                                if (status.contains("cancelled")) continue;

                                // ALWAYS SHOW ORDERS
                                displayList.add(o);

                                if (status.contains("delivered")) {
                                    deliveredCount++;
                                } else {
                                    assignedCount++;
                                }
                            }

                            txtAssignedCount.setText(String.valueOf(assignedCount));
                            txtDeliveredCount.setText(String.valueOf(deliveredCount));

                            if (adapter == null) {
                                adapter = new OrderAdapter(displayList, RiderDashboardActivity.this);
                                recyclerView.setAdapter(adapter);
                            } else {
                                adapter.updateList(displayList);
                            }

                        } else {
                            Toast.makeText(RiderDashboardActivity.this,
                                    "Server error",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Order>> call, Throwable t) {
                        Toast.makeText(RiderDashboardActivity.this,
                                "Connection Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ===================== LIFECYCLE FIX =====================

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders(); // immediate refresh
        handler.postDelayed(refreshRunnable, 10000);
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