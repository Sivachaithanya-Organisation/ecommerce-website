package com.marketrow.ecommerce.controller;

import com.marketrow.ecommerce.cart.Cart;
import com.marketrow.ecommerce.model.Category;
import com.marketrow.ecommerce.model.Product;
import com.marketrow.ecommerce.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductController {

    private final ProductService productService;
    private final Cart cart;

    public ProductController(ProductService productService, Cart cart) {
        this.productService = productService;
        this.cart = cart;
    }

    @GetMapping("/category/{slug}")
    public String categoryPage(@PathVariable String slug, Model model) {
        Category category = productService.getCategoryBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + slug));

        model.addAttribute("category", category);
        model.addAttribute("products", productService.getProductsByCategorySlug(slug));
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("cartItemCount", cart.getItemCount());
        return "category";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));

        model.addAttribute("product", product);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("cartItemCount", cart.getItemCount());
        return "product-detail";
    }
}
