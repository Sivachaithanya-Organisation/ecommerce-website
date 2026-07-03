package com.marketrow.ecommerce.controller;

import com.marketrow.ecommerce.cart.Cart;
import com.marketrow.ecommerce.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final ProductService productService;
    private final Cart cart;

    public HomeController(ProductService productService, Cart cart) {
        this.productService = productService;
        this.cart = cart;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("featuredProducts", productService.getFeaturedProducts());
        model.addAttribute("cartItemCount", cart.getItemCount());
        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam("q") String query, Model model) {
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("products", productService.searchProducts(query));
        model.addAttribute("searchQuery", query);
        model.addAttribute("cartItemCount", cart.getItemCount());
        return "search-results";
    }
}
