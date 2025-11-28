package org.bkv.orders.controllers;

import org.bkv.orders.entity.OrderEntity;
import org.bkv.orders.services.impls.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
class NotifyControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private NotifyController notifyController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getOrders_shouldReturnAllOrders() {

        OrderEntity order1 = new OrderEntity();
        OrderEntity order2 = new OrderEntity();
        List<OrderEntity> orders = Arrays.asList(order1, order2);

        when(orderService.findAll()).thenReturn(orders);

        ResponseEntity<List<OrderEntity>> response = notifyController.getOrders();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(orders, response.getBody());
        verify(orderService, times(1)).findAll();
    }

    @Test
    void getOrderById_shouldReturnOrder_whenOrderExists() {

        Long orderId = 1L;
        OrderEntity order = new OrderEntity();
        when(orderService.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        ResponseEntity<OrderEntity> response = notifyController.getOrderById(orderId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(order, response.getBody());
        verify(orderService, times(1)).findById(orderId);
    }

    @Test
    void getOrderById_shouldReturnNotFound_whenOrderDoesNotExist() {

        Long orderId = 1L;
        when(orderService.findById(orderId)).thenReturn(Optional.empty());

        ResponseEntity<OrderEntity> response = notifyController.getOrderById(orderId);

        assertNotNull(response);
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(orderService, times(1)).findById(orderId);
    }
}