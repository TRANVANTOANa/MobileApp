package com.example.hitcapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hitcapp.Product;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem; // Thêm import này nếu chưa có

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        CategoryAdapter.OnCategoryClickListener,
        ProductAdapter.OnProductActionListener {

    private RecyclerView productRecyclerView;
    private RecyclerView categoryRecyclerView;
    private SearchView searchView;
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private List<Product> originalProductList;
    private ImageView bannerImageView;
    private BottomNavigationView bottomNavigationView;
    private ImageButton cartImageButton;
    private ImageButton refreshButton;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private String url = "https://fakestoreapi.com/products";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> { // SỬA ID TẠI ĐÂY
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getData();
        // --- Ánh xạ và thiết lập các View ---
        searchView = findViewById(R.id.search_bar);
        bannerImageView = findViewById(R.id.banner_image);
        categoryRecyclerView = findViewById(R.id.category_recyclerview);
        productRecyclerView = findViewById(R.id.product_recyclerview);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        cartImageButton = findViewById(R.id.cartImageButton); // ĐÃ SỬA ID ĐỂ KHỚP VỚI activity_main.xml
        refreshButton = findViewById(R.id.refresh_button);

        // --- Thiết lập ảnh banner ---
        bannerImageView.setImageResource(R.drawable.baner); // Đảm bảo bạn có tài nguyên này

        // --- Thiết lập RecyclerView Danh mục (Horizontal) ---
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<Category> categories = getDummyCategories();
        categoryAdapter = new CategoryAdapter(categories, this);
        categoryRecyclerView.setAdapter(categoryAdapter);

        // --- Thiết lập RecyclerView Sản phẩm (Vertical) --
        originalProductList = getDummyProducts();
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this, originalProductList, this);
        productRecyclerView.setAdapter(productAdapter);

        // --- Thiết lập Search View Listener ---
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // ĐÃ XÓA PHƯƠNG THỨC public boolean onOnQueryTextSubmit(String query) {...} TRÙNG LẶP
            @Override
            public boolean onQueryTextSubmit(String query) { // Giữ lại phương thức override đúng tên
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productAdapter.getFilter().filter(newText);
                return true;
            }
        });

        // --- Thiết lập listener cho BottomNavigationView ---
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(MainActivity.this, "Bạn đang ở Trang chủ", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_promo) {
                Toast.makeText(MainActivity.this, "Chuyển đến Khuyến mãi", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, PromotionActivity.class)); // Đảm bảo PromotionActivity tồn tại
                return true;
            } else if (id == R.id.nav_account) {
                Toast.makeText(MainActivity.this, "Chuyển đến Tài khoản", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, AccountActivity.class)); // Đảm bảo AccountActivity tồn tại
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // --- Thiết lập OnClickListener cho ImageButton giỏ hàng ---
        cartImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Chuyển đến Giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });

        // --- THIẾT LẬP ONCLICKLISTENER CHO NÚT LÀM MỚI ---
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Đang làm mới dữ liệu...", Toast.LENGTH_SHORT).show();
                originalProductList = getDummyProducts();
                productAdapter.setProducts(originalProductList);
            }
        });
    }

    // --- Các phương thức tạo dữ liệu giả ---
    private List<Category> getDummyCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Khai vị", R.drawable.goi));
        categories.add(new Category("Món nhậu", R.drawable.mucsate));
        categories.add(new Category("Lẩu", R.drawable.lauthai));
        categories.add(new Category("Bia", R.drawable.bia));
        categories.add(new Category("Tráng miệng", R.drawable.traicay));
        return categories;
    }

    private List<Product> getDummyProducts() { // Dùng Product
        List<Product> products = new ArrayList<>();
        products.add(new Product("SRT001", "Cơm Chiên", 350000.0, R.drawable.comchien, "Cơm chiên vàng giòn, đậm đà hương vị, ăn kèm rau củ và topping hấp dẫn."));
        products.add(new Product("SRHN002", "Tóp Mỡ Chiên Giòn", 120000.0, R.drawable.topmo, "Tóp mỡ giòn rụm, beo béo, thơm ngon khó cưỡng."));
        products.add(new Product("SRKQ003", "Chả Chiên", 180000.0, R.drawable.cha, "Chả chiên vàng ruộm, thơm lừng, ăn kèm nước mắm chua ngọt."));
        products.add(new Product("SRMK004", "Gỏi", 700000.0, R.drawable.goi, "Món gỏi tươi mát với rau sống, thịt tôm, rưới nước sốt đậm đà."));
        products.add(new Product("SRR6005", "Bánh Xèo", 250000.0, R.drawable.bxeo, "Bánh xèo giòn rụm, nhân thịt tôm, ăn kèm rau sống và nước chấm."));
        products.add(new Product("DH006", "Đậu Hủ Chiên Giòn", 30000.0, R.drawable.dauhuchien, "Đậu hủ chiên vàng giòn, mềm mịn bên trong, thích hợp ăn chay."));
        return products;
    }

    // --- Triển khai các phương thức từ OnCategoryClickListener và OnProductActionListener ---
    @Override
    public void onCategoryClick(Category category) {
        Toast.makeText(this, "Bạn đã chọn danh mục: " + category.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBuyClick(Product product) { // Dùng Product
        CartManager.getInstance().addProduct(product, 1);
        Toast.makeText(this, "Đã thêm " + product.getName() + " vào giỏ hàng!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetailsClick(Product product) { // Dùng Product
        Toast.makeText(this, "Đang xem chi tiết: " + product.getName(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
        intent.putExtra("product", product); // Truyền đối tượng Product
        startActivity(intent);
    }
    private void getData() {
        // RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);

        // String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(getApplicationContext(), "Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Error :" + error.toString());
            }
        });

        mRequestQueue.add(mStringRequest);
    }
}