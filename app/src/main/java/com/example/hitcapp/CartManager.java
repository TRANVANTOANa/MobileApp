package com.example.hitcapp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CartManager {

    private static CartManager instance;
    private List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // Thêm sản phẩm vào giỏ hàng hoặc cập nhật số lượng nếu đã có
    public void addProduct(Product product, int quantity) {
        boolean found = false;
        for (CartItem item : cartItems) {
            if (item.getProduct().equals(product)) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }
        if (!found) {
            cartItems.add(new CartItem(product, quantity));
        }
    }

    // Cập nhật số lượng của một sản phẩm cụ thể
    public void updateCartItemQuantity(Product product, int newQuantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().equals(product)) {
                item.setQuantity(newQuantity);
                if (newQuantity <= 0) { // Tự động xóa nếu số lượng về 0 hoặc âm
                    removeProduct(product);
                }
                break;
            }
        }
    }

    // Xóa sản phẩm khỏi giỏ hàng
    public void removeProduct(Product product) {
        Iterator<CartItem> iterator = cartItems.iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getProduct().equals(product)) {
                iterator.remove();
                break;
            }
        }
    }

    // Lấy danh sách các mặt hàng trong giỏ hàng
    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems); // Trả về bản sao để tránh sửa đổi trực tiếp
    }

    // Tính tổng giá trị giỏ hàng
    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }

    // Lấy tổng số lượng các mặt hàng (số lượng duy nhất của sản phẩm)
    public int getCartSize() {
        return cartItems.size();
    }

    // Xóa toàn bộ giỏ hàng
    public void clearCart() {
        cartItems.clear();
    }
}