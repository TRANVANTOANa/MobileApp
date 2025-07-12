package com.example.hitcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private RequestQueue requestQueue;
    private static final String LOGIN_URL = "https://fakestoreapi.com/auth/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Map views from layout
        usernameEditText = findViewById(R.id.editTextText);
        passwordEditText = findViewById(R.id.editTextText2);
        loginButton = findViewById(R.id.button);

        // Set up click listener for login button
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Validate input
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập tài khoản và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Perform login request
            performLogin(username, password);
        });

        // Handle "Forgot Password?" click
        TextView forgotPasswordTextView = findViewById(R.id.textView4);
        forgotPasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, activity_forgot_password.class);
            startActivity(intent);
        });

        // Handle "Register here!" click
        TextView registerTextView = findViewById(R.id.textView5);
        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin(String username, String password) {
        // Create JSON object for request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", username);
            requestBody.put("password", password);
        } catch (JSONException e) {
            Toast.makeText(this, "Lỗi tạo yêu cầu đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Volley JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                LOGIN_URL,
                requestBody,
                response -> {
                    try {
                        // Check if token is present in response
                        if (response.has("token")) {
                            String token = response.getString("token");
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                            // Navigate to MainActivity and clear back stack
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: Không nhận được token", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Lỗi xử lý phản hồi từ server", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Handle error
                    String errorMessage = "Đăng nhập thất bại: ";
                    if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode == 401) {
                            errorMessage += "Tài khoản hoặc mật khẩu không đúng";
                        } else {
                            errorMessage += "Lỗi server (" + error.networkResponse.statusCode + ")";
                        }
                    } else {
                        errorMessage += "Không thể kết nối đến server";
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
        );

        // Add request to queue
        requestQueue.add(jsonObjectRequest);
    }
}