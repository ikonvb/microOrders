package org.bkv.orders.services.interfaces;

import org.bkv.orders.entity.OrderEntity;
import org.bkv.orders.models.OrderDto;

import java.util.List;
import java.util.Optional;

public interface IOrderService {

    List<OrderEntity> findAll();

    Optional<OrderEntity> findById(Long id);

    void saveOrder(OrderDto message);
}
