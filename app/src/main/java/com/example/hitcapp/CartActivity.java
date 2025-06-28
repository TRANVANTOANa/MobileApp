package com.example.hitcapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemChangeListener {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private TextView tvTotalPrice;
    private TextView tvEmptyCartMessage;
    private Button btnCheckout;
    private Button btnClearCart; // Nút xóa toàn bộ giỏ hàng
    private ImageButton btnBackCart; // Nút quay lại

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        // Đảm bảo ID này khớp với ID gốc của layout trong activity_cart.xml (tôi dùng cart_root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cart_root_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- Ánh xạ các View từ layout ---
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        tvTotalPrice = findViewById(R.id.tvTotalPrice); // Đã sửa ID thành tvTotalPrice
        tvEmptyCartMessage = findViewById(R.id.tvEmptyCartMessage);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnClearCart = findViewById(R.id.btnClearCart); // Đã sửa ID
        btnBackCart = findViewById(R.id.btnBackCart); // Đã sửa ID

        // --- Thiết lập RecyclerView ---
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, CartManager.getInstance().getCartItems(), this);
        cartRecyclerView.setAdapter(cartAdapter);

        // Cập nhật tổng tiền và trạng thái giỏ hàng khi khởi tạo
        updateCartUI();

        // --- Thiết lập Listener cho nút Checkout ---
        btnCheckout.setOnClickListener(v -> {
            if (CartManager.getInstance().getCartSize() == 0) {
                Toast.makeText(CartActivity.this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                intent.putExtra("TOTAL_PRICE", CartManager.getInstance().getTotalPrice());
                startActivity(intent);
            }
        });

        // --- Thiết lập Listener cho nút Xóa giỏ hàng ---
        btnClearCart.setOnClickListener(v -> {
            if (CartManager.getInstance().getCartSize() > 0) {
                CartManager.getInstance().clearCart();
                updateCartUI();
                Toast.makeText(CartActivity.this, "Giỏ hàng đã được xóa!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CartActivity.this, "Giỏ hàng đã trống, không cần xóa.", Toast.LENGTH_SHORT).show();
            }
        });


        // --- Thiết lập Listener cho nút Quay lại ---
        if (btnBackCart != null) {
            btnBackCart.setOnClickListener(v -> finish());
        }
    }

    // --- Phương thức cập nhật giao diện giỏ hàng ---
    private void updateCartUI() {
        double total = CartManager.getInstance().getTotalPrice();
        tvTotalPrice.setText(formatCurrency(total));

        if (CartManager.getInstance().getCartSize() == 0) {
            tvEmptyCartMessage.setVisibility(View.VISIBLE);
            cartRecyclerView.setVisibility(View.GONE);
            btnCheckout.setEnabled(false);
            btnClearCart.setEnabled(false);
        } else {
            tvEmptyCartMessage.setVisibility(View.GONE);
            cartRecyclerView.setVisibility(View.VISIBLE);
            btnCheckout.setEnabled(true);
            btnClearCart.setEnabled(true);
        }
        cartAdapter.setCartItems(CartManager.getInstance().getCartItems());
    }

    // --- Phương thức định dạng tiền tệ ---
    private String formatCurrency(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return format.format(amount).replace("₫", "đ");
    }

    // --- Triển khai các phương thức của OnCartItemChangeListener ---
    @Override
    public void onQuantityChanged() {
        updateCartUI();
    }

    @Override
    public void onCartItemRemoved() {
        updateCartUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cartAdapter != null) {
            updateCartUI();
        }
    }
}