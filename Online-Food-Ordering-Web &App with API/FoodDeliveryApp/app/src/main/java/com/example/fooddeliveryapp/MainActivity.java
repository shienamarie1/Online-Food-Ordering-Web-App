package com.example.fooddeliveryapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {

            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validation
            if (TextUtils.isEmpty(username)) {
                etUsername.setError("Enter username");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Enter password");
                return;
            }

            // ✅ ONLY RIDER LOGIN
            if (username.equals("rider") && password.equals("rider")) {

                Toast.makeText(this, "Rider Login Successful", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, RiderDashboardActivity.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
}