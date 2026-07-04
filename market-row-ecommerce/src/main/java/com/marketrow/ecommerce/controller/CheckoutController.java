package com.marketrow.ecommerce.controller;

import com.marketrow.ecommerce.cart.Cart;
import com.marketrow.ecommerce.dto.CheckoutForm;
import com.marketrow.ecommerce.model.Order;
import com.marketrow.ecommerce.service.OrderService;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CheckoutController {

    private final Cart cart;
    private final OrderService orderService;

    public CheckoutController(Cart cart, OrderService orderService) {
        this.cart = cart;
        this.orderService = orderService;
    }

    @GetMapping("/checkout")
    public String checkoutForm(Model model) {
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("checkoutForm", new CheckoutForm());
        model.addAttribute("cart", cart);
        model.addAttribute("cartItemCount", cart.getItemCount());
        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@Valid @ModelAttribute CheckoutForm checkoutForm,
                              BindingResult bindingResult,
                              Model model) {
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("cart", cart);
            model.addAttribute("cartItemCount", cart.getItemCount());
            return "checkout";
        }

        Order order = orderService.placeOrder(cart, checkoutForm);
        return "redirect:/order-confirmation/" + order.getOrderNumber();
    }

    @GetMapping("/order-confirmation/{orderNumber}")
    public String orderConfirmation(@PathVariable String orderNumber, Model model) {
        Order order = orderService.getByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderNumber));

        model.addAttribute("order", order);
        model.addAttribute("cartItemCount", 0);
        return "order-confirmation";
    }
}
