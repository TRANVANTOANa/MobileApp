package com.example.hitcapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private OnCartItemChangeListener listener;

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartItemChangeListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    public void setCartItems(List<CartItem> newCartItems) {
        this.cartItems = newCartItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = cartItem.getProduct();

        if (product.getImageResId() != 0) {
            holder.productImageView.setImageResource(product.getImageResId());
        } else {
            holder.productImageView.setImageResource(R.drawable.ic_product_placeholder);
        }

        holder.productNameTextView.setText(product.getName());
        holder.productPriceTextView.setText(formatCurrency(product.getPrice()));
        holder.quantityTextView.setText(String.valueOf(cartItem.getQuantity()));

        holder.btnDecreaseQuantity.setOnClickListener(v -> {
            if (cartItem.getQuantity() > 1) {
                cartItem.decreaseQuantity();
                CartManager.getInstance().updateCartItemQuantity(product, cartItem.getQuantity());
                holder.quantityTextView.setText(String.valueOf(cartItem.getQuantity()));
                if (listener != null) {
                    listener.onQuantityChanged();
                }
            } else {
                Toast.makeText(context, "Giảm số lượng thêm sẽ xóa sản phẩm.", Toast.LENGTH_SHORT).show();

            }
        });

        holder.btnIncreaseQuantity.setOnClickListener(v -> {
            cartItem.increaseQuantity();
            CartManager.getInstance().updateCartItemQuantity(product, cartItem.getQuantity());
            holder.quantityTextView.setText(String.valueOf(cartItem.getQuantity()));
            if (listener != null) {
                listener.onQuantityChanged();
            }
        });

        holder.btnRemoveItem.setOnClickListener(v -> {
            CartManager.getInstance().removeProduct(product);
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());

            Toast.makeText(context, "Đã xóa " + product.getName() + " khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
            if (listener != null) {
                listener.onCartItemRemoved();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView quantityTextView;
        Button btnDecreaseQuantity;   // <--- SỬA TẠI ĐÂY: từ ImageButton thành Button
        Button btnIncreaseQuantity;   // <--- SỬA TẠI ĐÂY: từ ImageButton thành Button
        ImageButton btnRemoveItem;    // Cái này vẫn là ImageButton, OK

        @SuppressLint("WrongViewCast") // Bạn có thể xóa dòng này sau khi sửa, hoặc giữ lại nếu muốn
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.cart_item_image);
            productNameTextView = itemView.findViewById(R.id.cart_item_name);
            productPriceTextView = itemView.findViewById(R.id.cart_item_price);
            quantityTextView = itemView.findViewById(R.id.cart_item_quantity);
            btnDecreaseQuantity = itemView.findViewById(R.id.btn_decrease_quantity);
            btnIncreaseQuantity = itemView.findViewById(R.id.btn_increase_quantity);
            btnRemoveItem = itemView.findViewById(R.id.btn_remove_item);
        }
    }

    public interface OnCartItemChangeListener {
        void onQuantityChanged();
        void onCartItemRemoved();
    }

    private String formatCurrency(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return format.format(amount).replace("₫", "đ");
    }
}