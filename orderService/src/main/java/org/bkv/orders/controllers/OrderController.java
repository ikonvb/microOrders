package org.bkv.orders.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.antlr.v4.runtime.misc.Pair;
import org.bkv.orders.dto.requests.CreateOrderRequest;
import org.bkv.orders.dto.responses.CreateOrderResponse;
import org.bkv.orders.entity.OrderEntity;
import org.bkv.orders.kafka.KafkaProducerApp;
import org.bkv.orders.mappers.Mappers;
import org.bkv.orders.models.OrderDto;
import org.bkv.orders.services.impls.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(path = "/api/order", produces = {MediaType.APPLICATION_JSON_VALUE})
public class OrderController {

    private final OrderService orderService;
    private final KafkaProducerApp kafkaProducerApp;

    @Autowired
    public OrderController(OrderService orderService, KafkaProducerApp kafkaProducerApp) {
        this.orderService = orderService;
        this.kafkaProducerApp = kafkaProducerApp;
    }

    @PostMapping(path = "/create", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<@NonNull CreateOrderResponse> createOrder(@Valid @NotNull @RequestBody CreateOrderRequest request) {

        OrderEntity orderEntity = OrderEntity.builder()
                .userId(request.userId())
                .productId(request.productId())
                .quantity(request.quantity())
                .build();

        Pair<Boolean, Double> canCreate = orderService.canCreateOrder(orderEntity);

        if (canCreate.a) {

            orderEntity.setPrice(canCreate.b);
            orderEntity.setTotalPrice(canCreate.b * orderEntity.getQuantity());

            OrderEntity savedOrder = orderService.saveOrder(orderEntity);

            OrderDto orderDto = Mappers.toOrderDto(savedOrder);

            kafkaProducerApp.sendMessage(orderDto);

            return ResponseEntity.ok(new CreateOrderResponse(orderDto.orderId(), true));
        } else {
            return ResponseEntity.ok(new CreateOrderResponse(0L, false));
        }
    }
}
