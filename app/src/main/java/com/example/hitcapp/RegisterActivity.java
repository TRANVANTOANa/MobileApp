package com.example.hitcapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // ĐÃ CẬP NHẬT URL MỚI CỦA BẠN TẠI ĐÂY
    private static final String MOCKAPI_BASE_URL = "https://6873e8dac75558e273559db4.mockapi.io/email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if(email.isEmpty()) {
                etEmail.setError("Email không được để trống");
                etEmail.requestFocus();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Email không hợp lệ");
                etEmail.requestFocus();
                return;
            }
            if(password.isEmpty()) {
                etPassword.setError("Mật khẩu không được để trống");
                etPassword.requestFocus();
                return;
            }
            if (password.length() < 6) {
                etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
                etPassword.requestFocus();
                return;
            }
            if(confirmPassword.isEmpty()) {
                etConfirmPassword.setError("Xác nhận mật khẩu không được để trống");
                etConfirmPassword.requestFocus();
                return;
            }
            if(!password.equals(confirmPassword)) {
                etConfirmPassword.setError("Mật khẩu và xác nhận mật khẩu không khớp");
                etConfirmPassword.requestFocus();
                Toast.makeText(this, "Mật khẩu và xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(email, password);
        });

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser(String email, String password) {
        User newUser = new User(email, password);
        String json = gson.toJson(newUser);

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(MOCKAPI_BASE_URL)
                .post(body)
                .build();

        executorService.execute(() -> {
            Response response = null;
            try {
                response = client.newCall(request).execute();

                if (response != null) {
                    if (response.isSuccessful()) {
                        final String responseBody = response.body() != null ? response.body().string() : "Empty response";
                        runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công! " + responseBody, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        final int statusCode = response.code();
                        final String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                        runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + statusCode + " - " + errorBody, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: Không nhận được phản hồi từ máy chủ.", Toast.LENGTH_LONG).show();
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } finally {
                if (response != null && response.body() != null) {
                    response.body().close();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}