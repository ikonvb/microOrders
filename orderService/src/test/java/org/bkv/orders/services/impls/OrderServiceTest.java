package org.bkv.orders.services.impls;

import inventory.Inventory;
import org.antlr.v4.runtime.misc.Pair;
import org.bkv.orders.dto.requests.CreateOrderRequest;
import org.bkv.orders.entity.OrderEntity;
import org.bkv.orders.grpc.InventoryGrpcClient;
import org.bkv.orders.mappers.OrderMapper;
import org.bkv.orders.models.OrderDto;
import org.bkv.orders.repo.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
class OrderServiceTest {

    @Mock
    private InventoryGrpcClient grpcClient;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void canCreateOrder_availableProduct_returnsTrueAndPrice() {

        OrderEntity order = new OrderEntity();
        order.setProductId(1L);
        order.setQuantity(5);

        Inventory.CheckProductResponse response = Inventory.CheckProductResponse.newBuilder()
                .setAvailable(true)
                .setPrice(100.0)
                .setDiscount(10.0)
                .build();

        when(grpcClient.checkProduct(1L, 5)).thenReturn(response);

        Pair<Boolean, Double> result = orderService.canCreateOrder(order);

        assertTrue(result.a);
        assertEquals(100.0, result.b);
        verify(grpcClient, times(1)).checkProduct(1L, 5);
    }

    @Test
    void canCreateOrder_unavailableProduct_returnsFalseAndZero() {

        OrderEntity order = new OrderEntity();
        order.setProductId(1L);
        order.setQuantity(5);

        Inventory.CheckProductResponse response = Inventory.CheckProductResponse.newBuilder()
                .setAvailable(false)
                .setPrice(100.0)
                .setDiscount(10.0)
                .build();

        when(grpcClient.checkProduct(1L, 5)).thenReturn(response);

        Pair<Boolean, Double> result = orderService.canCreateOrder(order);

        assertFalse(result.a);
        assertEquals(0.0, result.b);
    }

    @Test
    void saveOrder_callsRepositorySave() {

        OrderEntity order = new OrderEntity();
        when(orderRepository.save(order)).thenReturn(order);

        OrderEntity savedOrder = orderService.saveOrder(order);

        assertEquals(order, savedOrder);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void getOrderEntity_callsMapper() {

        CreateOrderRequest request = new CreateOrderRequest(1L, 1L, 10);
        OrderEntity entity = new OrderEntity();
        when(orderMapper.toOrderEntity(request)).thenReturn(entity);

        OrderEntity result = orderService.getOrderEntity(request);

        assertEquals(entity, result);
        verify(orderMapper, times(1)).toOrderEntity(request);
    }

    @Test
    void getOderDto_callsMapper() {

        OrderEntity entity = new OrderEntity();
        OrderDto dto = new OrderDto(1L, 1L, 1L, 10, 100.0, 200.0);
        when(orderMapper.toOrderDto(entity)).thenReturn(dto);

        OrderDto result = orderService.getOderDto(entity);

        assertEquals(dto, result);
        verify(orderMapper, times(1)).toOrderDto(entity);
    }
}