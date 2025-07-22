package com.example.hitcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.android.volley.toolbox.JsonArrayRequest; // For finding user by email
import com.android.volley.toolbox.JsonObjectRequest; // For updating user
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Corrected class name to follow PascalCase convention
public class activity_reset_password extends AppCompatActivity {

    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button submitNewPasswordButton;
    private ProgressBar progressBar;

    private RequestQueue requestQueue;
    private String userEmailToReset; // To hold the email passed from ForgotPasswordActivity

    // Your MockAPI URL - consistent across registration, login, forgot password
    private static final String MOCKAPI_BASE_URL = "https://6873e8dac75558e273559db4.mockapi.io/email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password); // Ensure layout matches

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestQueue = Volley.newRequestQueue(this);

        // Get the email passed from ForgotPasswordActivity
        if (getIntent().hasExtra("userEmail")) {
            userEmailToReset = getIntent().getStringExtra("userEmail");
        } else {
            // Handle case where email is not passed (e.g., redirect to login or show error)
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin email.", Toast.LENGTH_LONG).show();
            finish(); // Close this activity
            return;
        }

        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        submitNewPasswordButton = findViewById(R.id.submitNewPasswordButton);
        progressBar = findViewById(R.id.progressBar);

        submitNewPasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (newPassword.isEmpty()) {
                newPasswordEditText.setError("Vui lòng nhập mật khẩu mới.");
                newPasswordEditText.requestFocus();
                return;
            }
            if (confirmPassword.isEmpty()) {
                confirmPasswordEditText.setError("Vui lòng xác nhận mật khẩu mới.");
                confirmPasswordEditText.requestFocus();
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                confirmPasswordEditText.setError("Mật khẩu xác nhận không khớp.");
                confirmPasswordEditText.requestFocus();
                return;
            }
            if (newPassword.length() < 6) {
                newPasswordEditText.setError("Mật khẩu phải có ít nhất 6 ký tự.");
                newPasswordEditText.requestFocus();
                return;
            }

            // Perform password reset via API
            resetPasswordViaApi(userEmailToReset, newPassword);
        });
    }

    /**
     * Finds the user by email on MockAPI.io and then updates their password.
     * @param email The email of the user to reset.
     * @param newPassword The new password.
     */
    private void resetPasswordViaApi(String email, String newPassword) {
        progressBar.setVisibility(View.VISIBLE);
        submitNewPasswordButton.setEnabled(false);
        newPasswordEditText.setEnabled(false);
        confirmPasswordEditText.setEnabled(false);

        // Step 1: Find the user's ID by email
        String findUserUrl = MOCKAPI_BASE_URL + "?email=" + email;

        JsonArrayRequest findUserRequest = new JsonArrayRequest(
                Request.Method.GET,
                findUserUrl,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0) {
                            try {
                                JSONObject user = response.getJSONObject(0); // Get the first matching user
                                String userId = user.getString("id"); // Get the user's ID from MockAPI.io

                                // Step 2: Update the user's password using a PUT request
                                JSONObject updateBody = new JSONObject();
                                updateBody.put("password", newPassword); // Update the password field

                                JsonObjectRequest updatePasswordRequest = new JsonObjectRequest(
                                        Request.Method.PUT,
                                        MOCKAPI_BASE_URL + "/" + userId, // PUT to specific user ID
                                        updateBody,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject updateResponse) {
                                                progressBar.setVisibility(View.GONE);
                                                submitNewPasswordButton.setEnabled(true);
                                                newPasswordEditText.setEnabled(true);
                                                confirmPasswordEditText.setEnabled(true);

                                                Toast.makeText(activity_reset_password.this,
                                                        "Mật khẩu của bạn đã được đặt lại thành công! Vui lòng đăng nhập lại.",
                                                        Toast.LENGTH_LONG).show();

                                                Intent intent = new Intent(activity_reset_password.this, LoginActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                handleApiError(error, "Lỗi khi cập nhật mật khẩu: ");
                                            }
                                        }
                                );
                                requestQueue.add(updatePasswordRequest);

                            } catch (JSONException e) {
                                handleLocalError("Lỗi xử lý dữ liệu người dùng: " + e.getMessage());
                            }
                        } else {
                            handleLocalError("Không tìm thấy tài khoản với email này.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleApiError(error, "Lỗi khi tìm tài khoản: ");
                    }
                }
        );
        requestQueue.add(findUserRequest);
    }

    private void handleApiError(VolleyError error, String prefixMessage) {
        progressBar.setVisibility(View.GONE);
        submitNewPasswordButton.setEnabled(true);
        newPasswordEditText.setEnabled(true);
        confirmPasswordEditText.setEnabled(true);

        String errorMessage = prefixMessage;
        if (error.networkResponse != null) {
            errorMessage += "Mã lỗi " + error.networkResponse.statusCode;
            if (error.networkResponse.data != null) {
                try {
                    String errorData = new String(error.networkResponse.data);
                    // You might parse errorData to get a more specific message from the API
                    errorMessage += " - " + errorData;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            errorMessage += "Không thể kết nối đến máy chủ.";
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void handleLocalError(String message) {
        progressBar.setVisibility(View.GONE);
        submitNewPasswordButton.setEnabled(true);
        newPasswordEditText.setEnabled(true);
        confirmPasswordEditText.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}