package com.marketrow.ecommerce.cart;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A single line item held in the shopper's session cart.
 * This is a plain object (not a JPA entity) since the cart lives in the HTTP session.
 */
public class CartItem implements Serializable {

    private Long productId;
    private String productName;
    private String imageUrl;
    private BigDecimal price;
    private int quantity;

    public CartItem() {
    }

    public CartItem(Long productId, String productName, String imageUrl, BigDecimal price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
