package com.example.hitcapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Filterable {

    private Context context;
    private List<Product> productList; // Danh sách sản phẩm hiện đang hiển thị
    private List<Product> productListFull; // Bản sao đầy đủ của tất cả sản phẩm (dùng để lọc)
    private OnProductActionListener listener;

    public interface OnProductActionListener {
        void onBuyClick(Product product);
        void onDetailsClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> productList, OnProductActionListener listener) {
        this.context = context;
        this.productList = productList;
        // KHỞI TẠO productListFull LÚC NÀY
        this.productListFull = new ArrayList<>(productList);
        this.listener = listener;
    }

    // Phương thức MỚI: Cập nhật danh sách sản phẩm hiển thị trong RecyclerView
    public void setProducts(List<Product> newProductList) {
        this.productList.clear(); // Xóa danh sách cũ
        this.productList.addAll(newProductList); // Thêm danh sách mới
        notifyDataSetChanged(); // Thông báo cho RecyclerView rằng dữ liệu đã thay đổi
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("%,.0f VNĐ", product.getPrice()));
        holder.productImage.setImageResource(product.getImageResId());
        holder.productDescription.setText(product.getDescription());

        holder.buyButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBuyClick(product);
            }
        });

        holder.detailsButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDetailsClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        return productFilter;
    }

    private Filter productFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Product> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                // Nếu chuỗi tìm kiếm rỗng, hiển thị toàn bộ danh sách gốc
                filteredList.addAll(productListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Product item : productListFull) { // Lọc trên danh sách ĐẦY ĐỦ
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            productList.clear();
            productList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productDescription;
        Button buyButton;
        Button detailsButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productDescription = itemView.findViewById(R.id.product_description);
            buyButton = itemView.findViewById(R.id.buy_button);
            detailsButton = itemView.findViewById(R.id.details_button);
        }
    }
}