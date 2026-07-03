package com.marketrow.ecommerce.cart;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A shopping cart bound to the shopper's HTTP session.
 * Spring creates one instance per session automatically thanks to @Scope("session").
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Cart implements Serializable {

    private final Map<Long, CartItem> items = new LinkedHashMap<>();

    public void addItem(Long productId, String productName, String imageUrl, BigDecimal price, int quantity) {
        CartItem existing = items.get(productId);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            items.put(productId, new CartItem(productId, productName, imageUrl, price, quantity));
        }
    }

    public void updateQuantity(Long productId, int quantity) {
        CartItem item = items.get(productId);
        if (item != null) {
            if (quantity <= 0) {
                items.remove(productId);
            } else {
                item.setQuantity(quantity);
            }
        }
    }

    public void removeItem(Long productId) {
        items.remove(productId);
    }

    public void clear() {
        items.clear();
    }

    public Map<Long, CartItem> getItems() {
        return items;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int getItemCount() {
        return items.values().stream().mapToInt(CartItem::getQuantity).sum();
    }

    public BigDecimal getTotal() {
        return items.values().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
