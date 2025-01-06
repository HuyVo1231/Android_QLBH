package com.example.myapplication.model;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static CartManager instance;
    private final List<ProductModel> cartProducts;

    // Private constructor để ngăn việc tạo trực tiếp từ bên ngoài
    private CartManager() {
        cartProducts = new ArrayList<>();
    }

    // Trả về instance duy nhất
    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }


    // Thêm sản phẩm vào giỏ hàng
    public void addToCart(ProductModel product) {
        boolean exists = false;
        for (ProductModel cartProduct : cartProducts) {
            // Kiểm tra đã tồn tại bằng id, nếu đã có thì tăng 1.
            if (cartProduct.getId() == product.getId()) {
                cartProduct.setQuantity(cartProduct.getQuantity() + 1);
                exists = true;
                break;
            }
        }
        if (!exists) {
            // Nếu chưa có thì thêm vào giỏ hàng và set quantity = 1.
            product.setQuantity(1);
            cartProducts.add(product);
        }
    }



    // Lấy danh sách sản phẩm trong giỏ hàng
    public List<ProductModel> getCartProducts() {
        return cartProducts;
    }

    // Xóa sản phẩm khỏi giỏ hàng
    public void removeFromCart(ProductModel product) {
        cartProducts.remove(product);
    }

    // Xóa toàn bộ giỏ hàng
    public void clearCart() {
        cartProducts.clear();
    }
}
