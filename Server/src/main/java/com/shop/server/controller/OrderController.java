package com.shop.server.controller;

import com.shop.server.dto.OrderRequest;
import com.shop.server.dto.OrderResponse;
import com.shop.server.model.Order;
import com.shop.server.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(order -> {
                    OrderResponse response = new OrderResponse();
                    response.setPurchaseDate(order.getPurchaseDate());
                    response.setItemIds(order.getItemIds());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderRequest orderRequest) {
        Order order = new Order();
        order.setPurchaseDate(LocalDate.now());
        order.setItemIds(orderRequest.getItemIds());

        Order savedOrder = orderRepository.save(order);
        return ResponseEntity.ok(savedOrder.getId());
    }
}