package com.example.fooddeliveryapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import Model.User;
import Network.ApiService;
import Network.LoginResponse;
import Network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString();
            String pass = etPassword.getText().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(user, pass);
            }
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser(String username, String password) {

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.login(username, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    LoginResponse result = response.body();

                    if (result.isSuccess()) {

                        User user = result.getUser();

                        Toast.makeText(LoginActivity.this,
                                "Welcome " + user.getName(),
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this,
                                RiderDashboardActivity.class);

                        intent.putExtra("user_id", user.getId());
                        intent.putExtra("user_name", user.getName());

                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(LoginActivity.this,
                                result.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(LoginActivity.this,
                            "Server Error",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this,
                        "Network Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
