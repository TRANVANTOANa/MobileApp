package com.example.hitcapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale; // Thêm import này cho toLowerCase(Locale.getDefault())

public class MainActivity extends AppCompatActivity implements
        CategoryAdapter.OnCategoryClickListener,
        ProductAdapter.OnProductActionListener {

    // Khai báo các thành phần UI
    private RecyclerView productRecyclerView;
    private RecyclerView categoryRecyclerView;
    private SearchView searchView;
    private ImageView bannerImageView;
    private BottomNavigationView bottomNavigationView;
    private ImageButton cartImageButton;
    private ImageButton refreshButton;

    // Khai báo Adapters và Danh sách dữ liệu
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private List<Product> originalProductList; // Lưu trữ danh sách đầy đủ TẤT CẢ sản phẩm
    // currentProductList không cần thiết ở đây nữa vì ProductAdapter đã tự quản lý danh sách hiển thị

    @SuppressLint("MissingInflatedId") // Bỏ qua cảnh báo nếu ID có vẻ thiếu (đảm bảo ID XML của bạn chính xác)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- 1. Khởi tạo các thành phần UI bằng cách tìm ID của chúng ---
        searchView = findViewById(R.id.search_bar);
        bannerImageView = findViewById(R.id.banner_image);
        categoryRecyclerView = findViewById(R.id.category_recyclerview);
        productRecyclerView = findViewById(R.id.product_recyclerview);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        cartImageButton = findViewById(R.id.cartImageButton);
        refreshButton = findViewById(R.id.refresh_button);

        // --- 2. Thiết lập các phần tử UI ban đầu ---
        bannerImageView.setImageResource(R.drawable.baner);

        // --- 3. Thiết lập RecyclerView Danh mục (Horizontal) ---
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<Category> categories = getDummyCategories(); // Tải dữ liệu danh mục giả
        categoryAdapter = new CategoryAdapter(this, categories, this); // 'this' là Context và OnCategoryClickListener
        categoryRecyclerView.setAdapter(categoryAdapter);

        // --- 4. Tải dữ liệu Sản phẩm ban đầu (giả) ---
        // originalProductList sẽ chứa tất cả các sản phẩm ban đầu, không thay đổi
        originalProductList = getDummyProducts();

        // --- 5. Thiết lập RecyclerView Sản phẩm (Vertical) ---
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Khởi tạo ProductAdapter với danh sách đầy đủ ban đầu
        productAdapter = new ProductAdapter(this, new ArrayList<>(originalProductList), this);
        productRecyclerView.setAdapter(productAdapter);

        // --- 6. Thiết lập Listener cho SearchView để lọc ---
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Không cần thiết cho tìm kiếm tức thì
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("SearchDebug", "Search query changed to: " + newText);
                // Kích hoạt hoạt động lọc trong ProductAdapter
                productAdapter.getFilter().filter(newText);
                return true;
            }
        });

        // --- 7. Thiết lập Listener cho Bottom Navigation ---
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(MainActivity.this, "Bạn đang ở Trang chủ", Toast.LENGTH_SHORT).show();
                // Khi về trang chủ, hiển thị lại tất cả sản phẩm
                productAdapter.setProducts(originalProductList);
                searchView.setQuery("", false); // Xóa thanh tìm kiếm
                return true;
            } else if (id == R.id.nav_products) {
                Toast.makeText(this, "Chuyển đến Món ăn", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, activity_all_products.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                // finish(); // Tùy chọn: nếu activity_all_products thay thế hoàn toàn MainActivity
                return true;
            } else if (id == R.id.nav_promo) {
                Toast.makeText(MainActivity.this, "Chuyển đến Khuyến mãi", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, PromotionActivity.class));
                return true;
            } else if (id == R.id.nav_account) {
                Toast.makeText(MainActivity.this, "Chuyển đến Tài khoản", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, AccountActivity.class));
                return true;
            }
            return false;
        });

        // Đặt mục mặc định được chọn cho Bottom Navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // --- 8. Thiết lập Listener click cho các nút ---
        cartImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
            Toast.makeText(MainActivity.this, "Chuyển đến Giỏ hàng", Toast.LENGTH_SHORT).show();
        });

        refreshButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Đang làm mới dữ liệu...", Toast.LENGTH_SHORT).show();
            // Tải lại dữ liệu giả và cập nhật adapter để hiển thị toàn bộ sản phẩm
            originalProductList = getDummyProducts(); // Cập nhật lại danh sách gốc
            productAdapter.setProducts(new ArrayList<>(originalProductList)); // Cập nhật adapter với danh sách mới
            searchView.setQuery("", false); // Xóa thanh tìm kiếm sau khi làm mới
        });
    }

    /**
     * Cung cấp danh sách danh mục giả lập cho mục đích minh họa.
     * Đảm bảo các tài nguyên drawable (ví dụ: R.drawable.goi) tồn tại trong project của bạn.
     * @return Một danh sách các đối tượng Category.
     */
    private List<Category> getDummyCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Khai vị", R.drawable.goi));
        categories.add(new Category("Món nhậu", R.drawable.mucsate));
        categories.add(new Category("Lẩu", R.drawable.lauthai));
        categories.add(new Category("Bia", R.drawable.bia));
        categories.add(new Category("Tráng miệng", R.drawable.traicay));
        // Thêm một danh mục "Tất cả" để hiển thị lại toàn bộ sản phẩm
        categories.add(0, new Category("Tất cả", R.drawable.logo)); // Giả sử bạn có ic_all.png/xml
        return categories;
    }

    /**
     * Cung cấp danh sách sản phẩm giả lập cho mục đích minh họa.
     * Đảm bảo các tài nguyên drawable (ví dụ: R.drawable.comchien) tồn tại trong project của bạn.
     * @return Một danh sách các đối tượng Product.
     */
    private List<Product> getDummyProducts() {
        List<Product> products = new ArrayList<>();
        // Đảm bảo tên danh mục ở đây KHỚP CHÍNH XÁC với tên trong getDummyCategories()
        products.add(new Product("P001", "Cơm Chiên Hải Sản", 35000.0, R.drawable.comchien, "Cơm chiên vàng giòn, đậm đà hương vị hải sản.", "Món nhậu"));
        products.add(new Product("P002", "Tóp Mỡ Rim Mắm", 12000.0, R.drawable.topmo, "Tóp mỡ chiên giòn, rim nước mắm tỏi ớt thơm ngon.", "Khai vị"));
        products.add(new Product("P003", "Chả Lụa Chiên", 18000.0, R.drawable.cha, "Chả lụa chiên vàng ruộm, thơm lừng, dễ ăn.", "Khai vị"));
        products.add(new Product("P004", "Gỏi Ngó Sen Tôm Thịt", 70000.0, R.drawable.goi, "Món gỏi thanh mát, chua ngọt hài hòa.", "Khai vị"));
        products.add(new Product("P005", "Bánh Xèo Giòn Rụm", 25000.0, R.drawable.bxeo, "Bánh xèo miền Tây nhân tôm thịt, rau sống.", "Món nhậu"));
        products.add(new Product("P006", "Đậu Hủ Sốt Cà Chua", 30000.0, R.drawable.dauhuchien, "Đậu hủ mềm mịn, sốt cà chua đậm đà, món chay hấp dẫn.", "Món nhậu"));
        products.add(new Product("P007", "Mực Chiên Giòn", 65000.0, R.drawable.mucsate, "Mực tươi chiên vàng giòn, chấm tương ớt.", "Món nhậu"));
        products.add(new Product("P008", "Lẩu Thái Cay", 150000.0, R.drawable.lauthai, "Nồi lẩu Thái chua cay, thơm lừng, nhiều hải sản.", "Lẩu"));
        products.add(new Product("P009", "Bia Sài Gòn", 20000.0, R.drawable.bia, "Bia mát lạnh, giải khát.", "Bia"));
        products.add(new Product("P010", "Trái Cây Thập Cẩm", 45000.0, R.drawable.traicay, "Đĩa trái cây tươi ngon theo mùa.", "Tráng miệng"));
        return products;
    }

    /**
     * Callback khi một mục danh mục được click.
     * @param category Đối tượng Category đã được click.
     */
    @Override
    public void onCategoryClick(Category category) {
        Toast.makeText(this, "Bạn đã chọn danh mục: " + category.getName(), Toast.LENGTH_SHORT).show();

        // Xóa nội dung tìm kiếm cũ khi chọn danh mục mới
        searchView.setQuery("", false);

        // Lọc danh sách sản phẩm dựa trên danh mục được chọn
        filterProductsByCategory(category.getName());
    }

    /**
     * Phương thức mới để lọc sản phẩm theo tên danh mục.
     * @param categoryName Tên danh mục cần lọc.
     */
    private void filterProductsByCategory(String categoryName) {
        List<Product> filteredList = new ArrayList<>();
        // Nếu là "Tất cả" hoặc rỗng, hiển thị lại toàn bộ sản phẩm gốc
        if (categoryName == null || categoryName.toLowerCase(Locale.getDefault()).equals("tất cả")) {
            filteredList.addAll(originalProductList);
        } else {
            String lowerCaseCategoryName = categoryName.toLowerCase(Locale.getDefault());
            for (Product product : originalProductList) {
                if (product.getCategoryName() != null && product.getCategoryName().toLowerCase(Locale.getDefault()).equals(lowerCaseCategoryName)) {
                    filteredList.add(product);
                }
            }
        }
        // Cập nhật adapter với danh sách đã lọc
        productAdapter.setProducts(filteredList);
    }

    /**
     * Callback khi nút "Buy" trên một sản phẩm được click.
     * @param product Đối tượng Product liên quan đến nút được click.
     */
    @Override
    public void onBuyClick(Product product) {
        // Thêm sản phẩm vào CartManager với số lượng 1
        CartManager.getInstance().addProduct(product, 1);
        Toast.makeText(this, "Đã thêm " + product.getName() + " vào giỏ hàng!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Callback khi nút "Details" trên một sản phẩm được click.
     * @param product Đối tượng Product liên quan đến nút được click.
     */
    @Override
    public void onDetailsClick(Product product) {
        Toast.makeText(this, "Đang xem chi tiết: " + product.getName(), Toast.LENGTH_SHORT).show();
        // Bắt đầu ProductDetailActivity và truyền sản phẩm đã click
        Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
        intent.putExtra("product", product); // Lớp Product phải implement Serializable
        startActivity(intent);
    }
}