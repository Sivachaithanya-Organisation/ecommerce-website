package com.marketrow.ecommerce.controller;

import com.marketrow.ecommerce.cart.Cart;
import com.marketrow.ecommerce.model.Product;
import com.marketrow.ecommerce.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final Cart cart;
    private final ProductService productService;

    public CartController(Cart cart, ProductService productService) {
        this.cart = cart;
        this.productService = productService;
    }

    @GetMapping
    public String viewCart(Model model) {
        model.addAttribute("cart", cart);
        model.addAttribute("cartItemCount", cart.getItemCount());
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                             @RequestParam(defaultValue = "1") int quantity,
                             @RequestParam(required = false) String redirectTo) {
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        cart.addItem(product.getId(), product.getName(), product.getImageUrl(), product.getPrice(), quantity);

        return "redirect:" + (redirectTo != null ? redirectTo : "/cart");
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long productId, @RequestParam int quantity) {
        cart.updateQuantity(productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeItem(@RequestParam Long productId) {
        cart.removeItem(productId);
        return "redirect:/cart";
    }
}
