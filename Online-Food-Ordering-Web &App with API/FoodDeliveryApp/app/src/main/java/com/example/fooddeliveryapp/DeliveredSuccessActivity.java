package com.example.fooddeliveryapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DeliveredSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivered);

        Button btnBackHome = findViewById(R.id.btnBackHome);

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(DeliveredSuccessActivity.this, RiderDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
