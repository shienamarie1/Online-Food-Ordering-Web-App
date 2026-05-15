package com.example.fooddeliveryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import Network.ApiService;
import Network.BasicResponse;
import Network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etUsername, etPassword;
    Button btnRegister;
    TextView tvLogin, tvStatusMessage;
    LinearLayout registrationForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etRegName);
        etUsername = findViewById(R.id.etRegUsername);
        etPassword = findViewById(R.id.etRegPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        
        // These might need to be added to your layout if you want to show a "Pending" screen
        registrationForm = findViewById(R.id.registrationForm); 
        tvStatusMessage = findViewById(R.id.tvStatusMessage);

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(name, username, password);
        });

        tvLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private void registerUser(String name, String username, String password) {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.register(name, username, password).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        // Change UI to show it's pending admin approval
                        Toast.makeText(RegisterActivity.this, "Request sent! Waiting for Admin verification.", Toast.LENGTH_LONG).show();
                        
                        // Optional: Hide form and show a message
                        if (registrationForm != null) registrationForm.setVisibility(View.GONE);
                        if (tvStatusMessage != null) {
                            tvStatusMessage.setVisibility(View.VISIBLE);
                            tvStatusMessage.setText("Your registration is pending admin approval.\nPlease check back later.");
                        } else {
                            // If you haven't updated the layout yet, just go back to login
                            finish();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
