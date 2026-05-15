package com.example.fooddeliveryapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Adapter.OrderAdapter;
import Model.Order;
import Network.ApiService;
import Network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListActivity extends AppCompatActivity {

    LinearLayout navHome, navOrders, navDelivery, navProfile;
    RecyclerView recyclerView;
    int riderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_list);

        recyclerView = findViewById(R.id.recyclerOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        navHome = findViewById(R.id.navHome);
        navOrders = findViewById(R.id.navOrders);
        navDelivery = findViewById(R.id.navDelivery);
        navProfile = findViewById(R.id.navProfile);

        riderId = getIntent().getIntExtra("user_id", -1);

        if (riderId == -1) {
            Toast.makeText(this, "Invalid rider session", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchOrders();


        // NAVIGATION FIX

        FloatingHelper.setup(this,
                riderId,
                navHome,
                navOrders,
                navDelivery,
                navProfile
        );

    }
    @Override
    protected void onResume() {
        super.onResume();
        fetchOrders(); // 🔥 THIS FIXES AUTO REFRESH
    }

    // ================= NAVIGATION =================


    private void fetchOrders() {

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.getRiderOrders(String.valueOf(riderId))
                .enqueue(new Callback<List<Order>>() {

                    @Override
                    public void onResponse(Call<List<Order>> call,
                                           Response<List<Order>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            OrderAdapter adapter =
                                    new OrderAdapter(response.body(), OrderListActivity.this);

                            recyclerView.setAdapter(adapter);

                        } else {
                            Toast.makeText(OrderListActivity.this,
                                    "No assigned orders found",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Order>> call, Throwable t) {
                        Log.e("API_ERROR", t.getMessage() != null ? t.getMessage() : "Unknown error");
                        Toast.makeText(OrderListActivity.this,
                                "Network Error",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}