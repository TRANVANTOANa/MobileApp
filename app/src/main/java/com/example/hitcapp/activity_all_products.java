package com.example.hitcapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton; // Make sure this import exists
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class activity_all_products extends AppCompatActivity implements ProductAdapter.OnProductActionListener {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> allProductsList;
    private BottomNavigationView bottomNavigationView;
    private SearchView searchView;
    private ImageButton cartImageButton; // Khai báo ImageButton cho giỏ hàng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_products);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Initialize UI Components
        recyclerView = findViewById(R.id.recycler_view_products);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchView = findViewById(R.id.search_view);
        cartImageButton = findViewById(R.id.cartImageButton); // <--- Initialize the cart ImageButton here

        // 2. Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. Sample product data
        allProductsList = new ArrayList<>();
        allProductsList.add(new Product("1", "Gỏi Cuốn Tôm", 50000, R.drawable.goi, "Gỏi cuốn tôm tươi với sốt đậu phộng", "Khai vị"));
        allProductsList.add(new Product("2", "Mực Nướng Sa Tế", 120000, R.drawable.mucsate, "Mực nướng với gia vị cay", "Món nhậu"));
        allProductsList.add(new Product("3", "Đậu Hủ Chiên Giòn", 40000, R.drawable.dauhuchien, "Đậu phụ chiên giòn với rau thơm", "Món nhậu"));
        allProductsList.add(new Product("4", "Lẩu Thái Hải Sản", 250000, R.drawable.lauthai, "Lẩu hải sản với rau củ", "Lẩu"));
        allProductsList.add(new Product("5", "Cơm Chiên Dương Châu", 70000, R.drawable.comchien, "Cơm chiên kiểu Dương Châu với nguyên liệu đa dạng", "Món nhậu"));
        allProductsList.add(new Product("6", "Khay Trái Cây Tươi", 80000, R.drawable.traicay, "Khay trái cây tươi đầy màu sắc", "Tráng miệng"));
        allProductsList.add(new Product("7", "Bia Tiger", 20000, R.drawable.bia, "Bia Tiger Lager (lon)", "Bia"));
        allProductsList.add(new Product("8", "Tôm Nướng Muối Ớt", 150000, R.drawable.tomnuong, "Tôm nướng với chanh và ớt", "Món nhậu"));

        // 4. Initialize adapter
        productAdapter = new ProductAdapter(this, new ArrayList<>(allProductsList), this);
        recyclerView.setAdapter(productAdapter);

        // 5. Setup SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("ProductList", "Search query: " + newText);
                productAdapter.getFilter().filter(newText);
                return true;
            }
        });

        // 6. Setup BottomNavigationView
        bottomNavigationView.setSelectedItemId(R.id.nav_products);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(this, "Chuyển đến Trang chủ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity_all_products.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_products) {
                Toast.makeText(this, "Bạn đang ở trang Món ăn", Toast.LENGTH_SHORT).show();
                recyclerView.scrollToPosition(0);
                searchView.setQuery("", false);
                productAdapter.setProducts(new ArrayList<>(allProductsList));
                return true;
            } else if (id == R.id.nav_promo) {
                Toast.makeText(this, "Chuyển đến Khuyến Mãi", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity_all_products.this, PromotionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_account) {
                Toast.makeText(this, "Chuyển đến Tài khoản", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity_all_products.this, AccountActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        // 7. Setup Cart ImageButton Listener (NEW ADDITION)
        // This makes the cart icon in the top bar clickable
        cartImageButton.setOnClickListener(v -> {
            // Create an Intent to go to the CartActivity
            Intent intent = new Intent(activity_all_products.this, CartActivity.class);
            startActivity(intent); // Start the CartActivity
            Toast.makeText(activity_all_products.this, "Chuyển đến Giỏ hàng", Toast.LENGTH_SHORT).show();
        });
    }

    // --- ProductAdapter.OnProductActionListener implementations ---

    @Override
    public void onBuyClick(Product product) {
        if (product != null) {
            CartManager.getInstance().addProduct(product, 1);
            Toast.makeText(this, "Đã thêm " + product.getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetailsClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }
}