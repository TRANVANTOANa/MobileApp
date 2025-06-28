package com.example.hitcapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView; // Cần import nếu có TextView cho "Đã có tài khoản? Đăng nhập ngay!"
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin; // Thêm biến cho TextView "Đăng nhập ngay!"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register); // Đảm bảo đúng layout

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ các thành phần từ layout
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin); // Ánh xạ TextView "Đăng nhập ngay!"

        // Xử lý sự kiện click cho nút Đăng ký
        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            // Kiểm tra rỗng cho Email
            if(email.isEmpty()) {
                etEmail.setError("Email không được để trống"); // Hiển thị lỗi ngay trên EditText
                etEmail.requestFocus(); // Đặt con trỏ vào EditText này
                return;
            }
            // Kiểm tra định dạng email cơ bản (ví dụ: có chứa '@' và '.')
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Email không hợp lệ");
                etEmail.requestFocus();
                return;
            }

            // Kiểm tra rỗng cho Mật khẩu
            if(password.isEmpty()) {
                etPassword.setError("Mật khẩu không được để trống");
                etPassword.requestFocus();
                return;
            }
            // Kiểm tra độ dài mật khẩu (ví dụ: tối thiểu 6 ký tự)
            if (password.length() < 6) {
                etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
                etPassword.requestFocus();
                return;
            }

            // Kiểm tra rỗng cho Xác nhận mật khẩu
            if(confirmPassword.isEmpty()) {
                etConfirmPassword.setError("Xác nhận mật khẩu không được để trống");
                etConfirmPassword.requestFocus();
                return;
            }

            // Kiểm tra mật khẩu và xác nhận mật khẩu có khớp nhau không
            if(!password.equals(confirmPassword)) {
                etConfirmPassword.setError("Mật khẩu và xác nhận mật khẩu không khớp");
                etConfirmPassword.requestFocus();
                Toast.makeText(this, "Mật khẩu và xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- Logic đăng ký thực tế ---
            // Đây là nơi bạn sẽ thêm code để gửi thông tin đăng ký lên server (ví dụ: Firebase Authentication, API backend của bạn)
            // hoặc lưu vào cơ sở dữ liệu cục bộ.
            // Ví dụ:
            // FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            //      .addOnCompleteListener(task -> {
            //          if (task.isSuccessful()) {
            //              // Đăng ký thành công
            //              Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            //              Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            //              startActivity(intent);
            //              finish();
            //          } else {
            //              // Đăng ký thất bại
            //              Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            //          }
            //      });
            // -----------------------------

            // Nếu không có lỗi nào được tìm thấy (trước khi tích hợp backend)
            Toast.makeText(this, "Đăng ký thành công! (Chưa lưu dữ liệu)", Toast.LENGTH_LONG).show();

            // Chuyển về màn hình đăng nhập (LoginActivity)
            // Điều này hợp lý hơn sau khi đăng ký, để người dùng tự đăng nhập với tài khoản mới.
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Xóa các activity trên stack
            startActivity(intent);
            finish(); // Kết thúc RegisterActivity

            // Hoặc nếu bạn muốn chuyển thẳng đến MainActivity sau khi đăng ký:
            // Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            // startActivity(intent);
            // finish();
        });

        // Xử lý sự kiện click cho TextView "Đăng nhập ngay!"
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Kết thúc RegisterActivity khi chuyển sang LoginActivity
        });
    }
}