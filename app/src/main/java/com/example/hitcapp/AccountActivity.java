package com.example.hitcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView; // **IMPORTANT: Add this import**

public class AccountActivity extends AppCompatActivity {

    private TextView tvUserEmail;
    private TextView tvUserName;
    private Button btnLogout;
    private BottomNavigationView bottomNavigationView; // **Declare BottomNavigationView**

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ các View từ layout
        tvUserEmail = findViewById(R.id.tvAccountUserEmail);
        tvUserName = findViewById(R.id.tvAccountUserName);
        btnLogout = findViewById(R.id.btnLogout);
        bottomNavigationView = findViewById(R.id.bottom_navigation); // **Initialize BottomNavigationView**


        // --- Hiển thị thông tin người dùng (dữ liệu giả định) ---
        // Trong ứng dụng thực tế, bạn sẽ lấy dữ liệu này từ SharedPreferences, cơ sở dữ liệu cục bộ,
        // hoặc từ một đối tượng người dùng đang đăng nhập (ví dụ: từ Firebase, API của bạn).
        displayUserInfo("Botoanvlog@gmail.com", "Trần Văn Toàn");

        // Thiết lập OnClickListener cho nút Đăng xuất
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện hành động đăng xuất
                // Ví dụ: Xóa token đăng nhập, xóa thông tin người dùng khỏi SharedPreferences
                // FirebaseAuth.getInstance().signOut(); // Nếu dùng Firebase

                Toast.makeText(AccountActivity.this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();

                // Chuyển về màn hình đăng nhập (LoginActivity)
                // FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK sẽ xóa tất cả các Activity
                // trên stack và tạo một task mới cho LoginActivity, ngăn người dùng quay lại
                // các màn hình trước đó bằng nút Back.
                Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish(); // Kết thúc AccountActivity
            }
        });

        // **BEGIN: Bottom Navigation Bar Setup**
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            // IMPORTANT: Ensure these IDs match your res/menu/bottom_nav_menu.xml
            if (id == R.id.nav_home) {
                Toast.makeText(this, "Chuyển đến Trang chủ", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                // Optional: finish current activity if you don't want to keep it in back stack
                // finish();
                return true;
            } else if (id == R.id.nav_products) {
                Toast.makeText(this, "Chuyển đến Món ăn", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, activity_all_products.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_promo) {
                Toast.makeText(this, "Chuyển đến Khuyến mãi", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, PromotionActivity.class));
                // finish();
                return true;

            } else if (id == R.id.nav_account) {
                Toast.makeText(this, "Bạn đang ở Tài khoản", Toast.LENGTH_SHORT).show();
                return true; // Already on this screen
            }
            return false;
        });

        // **Highlight the current activity's menu item**
        bottomNavigationView.setSelectedItemId(R.id.nav_account);
        // **END: Bottom Navigation Bar Setup**
    }

    // Phương thức để hiển thị thông tin người dùng
    private void displayUserInfo(String email, String name) {
        tvUserEmail.setText(email);
        tvUserName.setText(name);
    }
}