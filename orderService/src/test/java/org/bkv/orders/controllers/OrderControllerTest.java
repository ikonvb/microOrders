package org.bkv.orders.controllers;

import org.antlr.v4.runtime.misc.Pair;
import org.bkv.orders.dto.requests.CreateOrderRequest;
import org.bkv.orders.dto.responses.CreateOrderResponse;
import org.bkv.orders.entity.OrderEntity;
import org.bkv.orders.kafka.KafkaProducerApp;
import org.bkv.orders.models.OrderDto;
import org.bkv.orders.services.impls.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderControllerTest {


    @Mock
    private OrderService orderService;

    @Mock
    private KafkaProducerApp kafkaProducerApp;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_success() {

        CreateOrderRequest request = new CreateOrderRequest(1L, 1L, 10);

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setQuantity(2);

        // Заглушки сервисов
        when(orderService.getOrderEntity(request)).thenReturn(orderEntity);
        when(orderService.canCreateOrder(orderEntity)).thenReturn(new Pair<>(true, 100.0));

        OrderEntity savedOrder = new OrderEntity();
        savedOrder.setOrderId(1L);
        savedOrder.setQuantity(2);
        savedOrder.setPrice(100.0);
        savedOrder.setTotalPrice(200.0);

        when(orderService.saveOrder(orderEntity)).thenReturn(savedOrder);

        OrderDto orderDto = new OrderDto(1L, 1L, 1L, 3, 100.0, 200.0);

        when(orderService.getOderDto(savedOrder)).thenReturn(orderDto);

        // Вызов контроллера
        ResponseEntity<CreateOrderResponse> response = orderController.createOrder(request);

        // Проверки
        assertTrue(response.getBody().status());
        assertEquals(1L, response.getBody().orderId());

        verify(orderService, times(1)).getOrderEntity(request);
        verify(orderService, times(1)).canCreateOrder(orderEntity);
        verify(orderService, times(1)).saveOrder(orderEntity);
        verify(orderService, times(1)).getOderDto(savedOrder);
        verify(kafkaProducerApp, times(1)).sendMessage(orderDto);
    }

    @Test
    void createOrder_cannotCreate() {
        CreateOrderRequest request = new CreateOrderRequest(1L, 1L, 10);

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setQuantity(5);

        when(orderService.getOrderEntity(request)).thenReturn(orderEntity);
        when(orderService.canCreateOrder(orderEntity)).thenReturn(new Pair<>(false, 0.0));

        ResponseEntity<CreateOrderResponse> response = orderController.createOrder(request);

        assertFalse(response.getBody().status());
        assertEquals(0L, response.getBody().orderId());

        verify(orderService, times(1)).getOrderEntity(request);
        verify(orderService, times(1)).canCreateOrder(orderEntity);
        verify(orderService, never()).saveOrder(any());
        verify(orderService, never()).getOderDto(any());
        verify(kafkaProducerApp, never()).sendMessage(any());
    }
}