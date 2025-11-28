package org.bkv.orders.services.impls;

import org.bkv.orders.entity.OrderEntity;
import org.bkv.orders.mappers.OrderMapper;
import org.bkv.orders.models.OrderDto;
import org.bkv.orders.repo.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnAllOrders() {

        OrderEntity order1 = new OrderEntity();
        OrderEntity order2 = new OrderEntity();
        List<OrderEntity> orders = Arrays.asList(order1, order2);
        when(orderRepository.findAll()).thenReturn(orders);

        List<OrderEntity> result = orderService.findAll();

        assertEquals(orders, result);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnOrder_whenOrderExists() {

        Long orderId = 1L;
        OrderEntity order = new OrderEntity();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Optional<OrderEntity> result = orderService.findById(orderId);

        assertTrue(result.isPresent());
        assertEquals(order, result.get());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void findById_shouldReturnEmpty_whenOrderDoesNotExist() {

        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Optional<OrderEntity> result = orderService.findById(orderId);

        assertFalse(result.isPresent());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void saveOrder_shouldSaveOrderSuccessfully() {

        OrderDto dto = new OrderDto(1L, 1L, 1L, 3, 10L, 30L);
        OrderEntity entity = new OrderEntity();
        when(orderMapper.toOrderEntity(dto)).thenReturn(entity);
        when(orderRepository.save(entity)).thenReturn(entity);

        orderService.saveOrder(dto);

        verify(orderMapper, times(1)).toOrderEntity(dto);
        verify(orderRepository, times(1)).save(entity);
    }

    @Test
    void saveOrder_shouldThrowException_whenRepositoryFails() {

        OrderDto dto = new OrderDto(1L, 1L, 1L, 3, 10L, 30L);
        OrderEntity entity = new OrderEntity();
        when(orderMapper.toOrderEntity(dto)).thenReturn(entity);
        doThrow(new RuntimeException("DB error")).when(orderRepository).save(entity);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> orderService.saveOrder(dto));
        assertEquals("DB error", thrown.getMessage());
        verify(orderMapper, times(1)).toOrderEntity(dto);
        verify(orderRepository, times(1)).save(entity);
    }
}