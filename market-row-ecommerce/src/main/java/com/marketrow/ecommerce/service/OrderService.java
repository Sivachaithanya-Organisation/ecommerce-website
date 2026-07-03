package com.marketrow.ecommerce.service;

import com.marketrow.ecommerce.cart.Cart;
import com.marketrow.ecommerce.cart.CartItem;
import com.marketrow.ecommerce.dto.CheckoutForm;
import com.marketrow.ecommerce.model.Order;
import com.marketrow.ecommerce.model.OrderItem;
import com.marketrow.ecommerce.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order placeOrder(Cart cart, CheckoutForm form) {
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomerName(form.getCustomerName());
        order.setEmail(form.getEmail());
        order.setPhone(form.getPhone());
        order.setAddressLine(form.getAddressLine());
        order.setCity(form.getCity());
        order.setState(form.getState());
        order.setZipCode(form.getZipCode());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("CONFIRMED");
        order.setTotalAmount(cart.getTotal());

        for (CartItem cartItem : cart.getItems().values()) {
            OrderItem orderItem = new OrderItem(
                    cartItem.getProductId(),
                    cartItem.getProductName(),
                    cartItem.getImageUrl(),
                    cartItem.getPrice(),
                    cartItem.getQuantity()
            );
            order.addOrderItem(orderItem);
        }

        Order saved = orderRepository.save(order);
        cart.clear();
        return saved;
    }

    public Optional<Order> getByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    private String generateOrderNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "MR-" + datePart + "-" + randomPart;
    }
}
