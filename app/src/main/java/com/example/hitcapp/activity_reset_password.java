package com.example.hitcapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler; // Import Handler
import android.os.Looper; // Import Looper for Handler
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

public class activity_reset_password extends AppCompatActivity {

    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button submitNewPasswordButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ các phần tử UI từ layout
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        submitNewPasswordButton = findViewById(R.id.submitNewPasswordButton);
        progressBar = findViewById(R.id.progressBar);

        // Thiết lập sự kiện click cho nút "Xác nhận"
        submitNewPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = newPasswordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                // Kiểm tra và xác thực đầu vào
                if (newPassword.isEmpty()) {
                    newPasswordEditText.setError("Vui lòng nhập mật khẩu mới.");
                    newPasswordEditText.requestFocus();
                } else if (confirmPassword.isEmpty()) {
                    confirmPasswordEditText.setError("Vui lòng xác nhận mật khẩu mới.");
                    confirmPasswordEditText.requestFocus();
                } else if (!newPassword.equals(confirmPassword)) {
                    confirmPasswordEditText.setError("Mật khẩu xác nhận không khớp.");
                    confirmPasswordEditText.requestFocus();
                } else if (newPassword.length() < 6) { // Yêu cầu mật khẩu tối thiểu 6 ký tự
                    newPasswordEditText.setError("Mật khẩu phải có ít nhất 6 ký tự.");
                    newPasswordEditText.requestFocus();
                } else {
                    // Nếu tất cả xác thực đều OK, giả lập quá trình đặt lại mật khẩu
                    simulatePasswordReset(newPassword);
                }
            }
        });
    }

    /**
     * Giả lập quá trình đặt lại mật khẩu.
     * Phương thức này hiển thị thanh tiến trình, vô hiệu hóa UI,
     * sau đó giả vờ thực hiện tác vụ và cung cấp phản hồi.
     *
     * @param newPassword Mật khẩu mới được người dùng nhập (không được sử dụng thực tế trong ví dụ này).
     */
    private void simulatePasswordReset(String newPassword) {
        // Hiển thị thanh tiến trình và vô hiệu hóa UI
        progressBar.setVisibility(View.VISIBLE);
        submitNewPasswordButton.setEnabled(false);
        newPasswordEditText.setEnabled(false);
        confirmPasswordEditText.setEnabled(false);

        // Sử dụng Handler để tạo độ trễ giả lập "cuộc gọi API"
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // --- Giả lập phản hồi ---
            // Luôn giả lập thành công để đơn giản.
            // Nếu muốn giả lập lỗi, bạn có thể dùng 'boolean success = Math.random() > 0.3;'
            boolean success = true; // Giả lập luôn thành công

            // Luôn ẩn thanh tiến trình và kích hoạt lại UI khi tác vụ hoàn thành
            progressBar.setVisibility(View.GONE);
            submitNewPasswordButton.setEnabled(true);
            newPasswordEditText.setEnabled(true);
            confirmPasswordEditText.setEnabled(true);

            if (success) {
                Toast.makeText(activity_reset_password.this,
                        "Mật khẩu của bạn đã được đặt lại thành công! Vui lòng đăng nhập lại.",
                        Toast.LENGTH_LONG).show();
                // Sau khi thành công, chuyển người dùng về màn hình đăng nhập
                // và xóa stack Activity để không quay lại được nữa.
                Intent intent = new Intent(activity_reset_password.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                // Trường hợp này sẽ không xảy ra với 'success = true;'
                Toast.makeText(activity_reset_password.this,
                        "Lỗi: Không thể đặt lại mật khẩu. Vui lòng thử lại.",
                        Toast.LENGTH_LONG).show();
            }
        }, 2000); // Giả lập độ trễ 2 giây
    }
}