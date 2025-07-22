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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Corrected class name to follow PascalCase convention
public class activity_forgot_password extends AppCompatActivity { // Changed class name

    private EditText emailEditText;
    private Button resetPasswordButton;
    private ProgressBar progressBar;
    private RequestQueue requestQueue;

    private static final String MOCKAPI_BASE_URL = "https://6873e8dac75558e273559db4.mockapi.io/email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestQueue = Volley.newRequestQueue(this);

        emailEditText = findViewById(R.id.emailEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        progressBar = findViewById(R.id.progressBar);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();

                if (email.isEmpty()) {
                    emailEditText.setError("Vui lòng nhập địa chỉ email.");
                    emailEditText.requestFocus();
                } else if (!isValidEmail(email)) {
                    emailEditText.setError("Định dạng email không hợp lệ.");
                    emailEditText.requestFocus();
                } else {
                    sendPasswordResetRequest(email);
                }
            }
        });
    }

    /**
     * Gửi yêu cầu đặt lại mật khẩu đến backend (MockAPI.io để kiểm tra tồn tại email).
     * Phương thức này xử lý trạng thái UI (đang tải, vô hiệu hóa các phần tử).
     *
     * @param email Địa chỉ email do người dùng cung cấp.
     */
    private void sendPasswordResetRequest(String email) {
        progressBar.setVisibility(View.VISIBLE);
        resetPasswordButton.setEnabled(false);
        emailEditText.setEnabled(false);

        String urlWithFilter = MOCKAPI_BASE_URL + "?email=" + email;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                urlWithFilter,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        progressBar.setVisibility(View.GONE);
                        resetPasswordButton.setEnabled(true);
                        emailEditText.setEnabled(true);

                        if (response.length() > 0) {
                            // Email found in MockAPI.io
                            Toast.makeText(activity_forgot_password.this,
                                    "Nếu email của bạn tồn tại, một liên kết đặt lại mật khẩu đã được gửi đến bạn.",
                                    Toast.LENGTH_LONG).show();

                            // ONLY transition if email is found, and pass the email
                            Intent intent = new Intent(activity_forgot_password.this, activity_reset_password.class);
                            intent.putExtra("userEmail", email); // Pass the email to the next activity
                            startActivity(intent);
                            finish(); // Close ForgotPasswordActivity
                        } else {
                            // Email NOT found in MockAPI.io
                            // Display generic message, but DO NOT navigate
                            Toast.makeText(activity_forgot_password.this,
                                    "Nếu email của bạn tồn tại, một liên kết đặt lại mật khẩu đã được gửi đến bạn.",
                                    Toast.LENGTH_LONG).show();
                            // Stay on this screen
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        resetPasswordButton.setEnabled(true);
                        emailEditText.setEnabled(true);

                        String errorMessage = "Lỗi kết nối: ";
                        if (error.networkResponse != null) {
                            errorMessage += "Mã lỗi " + error.networkResponse.statusCode;
                        } else {
                            errorMessage += "Không thể kết nối đến máy chủ.";
                        }
                        Toast.makeText(activity_forgot_password.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    /**
     * Xác thực định dạng email cơ bản.
     */
    private boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}