package com.example.hitcapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

// Đổi tên class từ activity_forgot_password thành ForgotPasswordActivity
// để tuân thủ quy ước đặt tên (PascalCase cho tên class)
public class activity_forgot_password extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordButton;
    private ProgressBar progressBar;

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
                }
                else {
                    sendPasswordResetRequest(email);
                }
            }
        });
    }

    /**
     * Gửi yêu cầu đặt lại mật khẩu đến backend (giả lập).
     * Phương thức này xử lý trạng thái UI (đang tải, vô hiệu hóa các phần tử) và giả lập kết quả.
     *
     * @param email Địa chỉ email do người dùng cung cấp.
     */
    private void sendPasswordResetRequest(String email) {
        // Hiển thị thanh tiến trình và vô hiệu hóa UI để ngăn nhiều yêu cầu
        progressBar.setVisibility(View.VISIBLE);
        resetPasswordButton.setEnabled(false);
        emailEditText.setEnabled(false);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            boolean success;
            String message;

            // Ép cứng toan@gmail.com luôn thành công
            if (email.equals("toan@gmail.com")) {
                success = true;
                message = "Yêu cầu đặt lại mật khẩu thành công!"; // Thông báo ngắn gọn hơn
            } else {
                // Đối với các email khác, giả lập thành công/thất bại ngẫu nhiên
                success = Math.random() > 0.3; // 70% cơ hội thành công
                if (success) {
                    message = "Yêu cầu đặt lại mật khẩu thành công!";
                } else {
                    message = "Lỗi: Không tìm thấy tài khoản với địa chỉ email này. Vui lòng thử lại.";
                }
            }

            progressBar.setVisibility(View.GONE);
            resetPasswordButton.setEnabled(true);
            emailEditText.setEnabled(true);

            if (success) {
                Toast.makeText(activity_forgot_password.this, message, Toast.LENGTH_SHORT).show(); // Dùng LENGTH_SHORT cho thông báo chuyển tiếp

                // *** ĐÂY LÀ PHẦN THAY ĐỔI: CHUYỂN SANG ResetPasswordActivity ***
                Intent intent = new Intent(activity_forgot_password.this, activity_reset_password.class);

                startActivity(intent);
                finish(); // Đóng ForgotPasswordActivity sau khi chuyển hướng
            } else {
                Toast.makeText(activity_forgot_password.this, message, Toast.LENGTH_LONG).show();
            }
        }, 2000); // Giả lập độ trễ 2 giây
    }

    /**
     * Xác thực định dạng email cơ bản.
     */
    private boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}