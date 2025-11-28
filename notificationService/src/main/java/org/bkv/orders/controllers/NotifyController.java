package org.bkv.orders.controllers;

import lombok.NonNull;
import org.bkv.orders.entity.OrderEntity;
import org.bkv.orders.services.impls.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotifyController {

    private final OrderService orderService;

    @Autowired
    public NotifyController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(path = "/getOrders")
    public ResponseEntity<@NonNull List<OrderEntity>> getOrders() {
        List<OrderEntity> orders = orderService.findAll();
        return ResponseEntity.ok(orders);
    }

    @GetMapping(path = "/getOrderById/{id}")
    public ResponseEntity<@NonNull OrderEntity> getOrderById(@PathVariable Long id) {
        Optional<OrderEntity> order = orderService.findById(id);
        return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
