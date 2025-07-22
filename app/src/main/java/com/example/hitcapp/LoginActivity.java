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
import com.android.volley.toolbox.JsonArrayRequest; // <--- Changed to JsonArrayRequest
import com.android.volley.toolbox.Volley;

import org.json.JSONArray; // <--- Added
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private RequestQueue requestQueue;

    // Use the same base URL as your RegisterActivity
    private static final String MOCKAPI_BASE_URL = "https://6873e8dac75558e273559db4.mockapi.io/email"; // Your MockAPI URL

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

        requestQueue = Volley.newRequestQueue(this);

        usernameEditText = findViewById(R.id.editTextText);
        passwordEditText = findViewById(R.id.editTextText2);
        loginButton = findViewById(R.id.button);

        loginButton.setOnClickListener(v -> {
            String email = usernameEditText.getText().toString().trim(); // Changed to email
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            performLogin(email, password);
        });

        TextView forgotPasswordTextView = findViewById(R.id.textView4);
        forgotPasswordTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, activity_forgot_password.class);
            startActivity(intent);
        });

        TextView registerTextView = findViewById(R.id.textView5);
        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin(String email, String password) {
        // MockAPI doesn't have a direct login endpoint.
        // We'll fetch all users and then check credentials locally.
        // For production, you'd use a proper backend for authentication.

        // Construct the URL to filter by email (optional, but good for efficiency)
        // Note: MockAPI's filtering might be basic. It's often better to fetch all and filter client-side if performance isn't critical
        // or if your MockAPI doesn't support complex queries.
        String urlWithFilter = MOCKAPI_BASE_URL + "?email=" + email;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, // We're using GET to retrieve existing users
                urlWithFilter, // Or just MOCKAPI_BASE_URL if you prefer to filter manually after getting all
                null, // No request body for GET
                response -> {
                    boolean loggedIn = false;
                    try {
                        // Iterate through the array of users received from MockAPI
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject user = response.getJSONObject(i);
                            String storedEmail = user.getString("email");
                            String storedPassword = user.getString("password"); // Assuming password is stored directly (not hashed)

                            // Check if email and password match
                            if (storedEmail.equals(email) && storedPassword.equals(password)) {
                                loggedIn = true;
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                break; // Exit loop once user is found
                            }
                        }

                        if (!loggedIn) {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(this, "Lỗi xử lý dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage = "Đăng nhập thất bại: ";
                    if (error.networkResponse != null) {
                        errorMessage += "Lỗi server (" + error.networkResponse.statusCode + ")";
                    } else {
                        errorMessage += "Không thể kết nối đến server";
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(jsonArrayRequest);
    }
}