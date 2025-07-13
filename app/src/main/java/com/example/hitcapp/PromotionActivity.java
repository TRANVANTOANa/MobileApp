package com.example.hitcapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView; // Make sure this import is present

import java.util.ArrayList;
import java.util.List;

public class PromotionActivity extends AppCompatActivity {

    private RecyclerView promotionRecyclerView;
    private PromotionAdapter promotionAdapter;
    private List<PromotionItem> promotionList;
    private BottomNavigationView bottomNavigationView; // Declare BottomNavigationView here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_promotion);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Initialize RecyclerView
        promotionRecyclerView = findViewById(R.id.promotionRecyclerView);
        promotionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. Prepare Dummy Data for Promotions
        promotionList = getDummyPromotions();

        // 3. Initialize and Set up Adapter for Promotions
        promotionAdapter = new PromotionAdapter(promotionList);
        promotionRecyclerView.setAdapter(promotionAdapter);

        // 4. Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 5. Set up BottomNavigationView Listener for navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            // IMPORTANT: Ensure these IDs match your res/menu/bottom_nav_menu.xml
            if (id == R.id.nav_home) { // ID for Home
                // Navigate to MainActivity
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
            } else if (id == R.id.nav_promo) { // ID for Promotions
                // Already on PromotionActivity, just show a toast or do nothing
                Toast.makeText(this, "Bạn đang ở Khuyến mãi", Toast.LENGTH_SHORT).show();
                return true; // Return true to indicate item is handled

            } else if (id == R.id.nav_account) { // ID for Profile/Account
                // Navigate to AccountActivity
                Toast.makeText(this, "Chuyển đến Tài khoản", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AccountActivity.class));
                // finish();
                return true;
            }
            return false;
        });

        // 6. Highlight the current activity's menu item
        // This ensures the "Khuyến mãi" item is selected when PromotionActivity is displayed.
        bottomNavigationView.setSelectedItemId(R.id.nav_promo);
    }

    // Function to create dummy data for promotions
    private List<PromotionItem> getDummyPromotions() {
        List<PromotionItem> promotions = new ArrayList<>();
        // Make sure these drawables exist in res/drawable
        promotions.add(new PromotionItem(
                "COMBO NHẬU 3 MÓN CHỈ 199K",
                "Lai rai cực đã với 3 món nhậu siêu ngon – giá chỉ 199.000 VNĐ. Có giới hạn, nhanh tay nào!",
                R.drawable.mixaobo));

        promotions.add(new PromotionItem(
                "TẶNG NGAY 1 LON BIA CHO ĐƠN TRÊN 300K",
                "Đặt hàng từ 300.000 VNĐ – tặng ngay 1 lon bia mát lạnh, nhậu càng đã!",
                R.drawable.bia));

        promotions.add(new PromotionItem(
                "MÓN NHẬU HOT - GIẢM ĐẾN 30%",
                "Gân bò cháy tỏi, tôm rang muối, ếch chiên bơ... đồng loạt giảm giá đến 30%.",
                R.drawable.tomnuong));

        promotions.add(new PromotionItem(
                "FREESHIP ĐÊM CUỐI TUẦN",
                "Miễn phí giao hàng từ 19h - 23h mỗi thứ Sáu & Bảy. Bạn lo nhậu, tụi mình lo ship!",
                R.drawable.mucsate));

        return promotions;
    }
}