package com.example.hitcapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView; // Thêm import này
import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    // Khai báo biến để lưu trữ đối tượng Product hiện tại
    private Product currentProduct;
    // Khai báo biến cho BottomNavigationView
    private BottomNavigationView bottomNavigationView; // DÒNG NÀY ĐÃ ĐƯỢC THÊM/SỬA

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.product_detail_root_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ BottomNavigationView từ layout XML của bạn
        // Đảm bảo rằng bạn có một BottomNavigationView với id="@+id/bottom_navigation" trong activity_product_detail.xml
        bottomNavigationView = findViewById(R.id.bottom_navigation); // DÒNG NÀY ĐÃ ĐƯỢC THÊM

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(ProductDetailActivity.this, "Chuyển đến Trang chủ", Toast.LENGTH_SHORT).show(); // SỬA TỪ MainActivity.this
                Intent intent = new Intent(ProductDetailActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish(); // Đóng Activity hiện tại
                return true;
            } else if (id == R.id.nav_promo) {
                Toast.makeText(ProductDetailActivity.this, "Chuyển đến Khuyến mãi", Toast.LENGTH_SHORT).show(); // SỬA TỪ MainActivity.this
                startActivity(new Intent(ProductDetailActivity.this, PromotionActivity.class));
                finish(); // Đóng Activity hiện tại
                return true;
            } else if (id == R.id.nav_account) {
                Toast.makeText(ProductDetailActivity.this, "Chuyển đến Tài khoản", Toast.LENGTH_SHORT).show(); // SỬA TỪ MainActivity.this
                startActivity(new Intent(ProductDetailActivity.this, AccountActivity.class));
                finish(); // Đóng Activity hiện tại
                return true;
            }
            return false;
        });

        // Đặt mục được chọn mặc định. Nếu bạn không muốn nó được chọn mặc định
        // hoặc muốn nó phản ánh mục mà bạn đã đến từ đó, hãy điều chỉnh logic này.
        // bottomNavigationView.setSelectedItemId(R.id.nav_home); // DÒNG NÀY CÓ THỂ TÙY CHỌN BỎ ĐI

        // Ánh xạ các View còn lại
        ImageView productImage = findViewById(R.id.detail_product_image);
        TextView productName = findViewById(R.id.detail_product_name);
        TextView productPrice = findViewById(R.id.detail_product_price);
        TextView productDescription = findViewById(R.id.detail_product_description);
        Button buyButton = findViewById(R.id.detail_buy_button);

        // Lấy đối tượng Product từ Intent
        if (getIntent().hasExtra("product")) {
            currentProduct = (Product) getIntent().getSerializableExtra("product");

            if (currentProduct != null) {
                productName.setText(currentProduct.getName());
                productPrice.setText(formatCurrency(currentProduct.getPrice()));
                productImage.setImageResource(currentProduct.getImageResId());
                productDescription.setText(currentProduct.getDescription());

                buyButton.setOnClickListener(v -> {
                    CartManager.getInstance().addProduct(currentProduct, 1);
                    Toast.makeText(ProductDetailActivity.this, "Đã thêm " + currentProduct.getName() + " vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(this, "Không thể tải thông tin sản phẩm.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin sản phẩm.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String formatCurrency(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return format.format(amount).replace("₫", "đ");
    }
}