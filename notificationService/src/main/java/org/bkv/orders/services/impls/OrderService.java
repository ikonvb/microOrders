package org.bkv.orders.services.impls;


import lombok.AllArgsConstructor;
import org.bkv.orders.entity.OrderEntity;
import org.bkv.orders.mappers.Mappers;
import org.bkv.orders.models.OrderDto;
import org.bkv.orders.repo.OrderRepository;
import org.bkv.orders.services.interfaces.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService implements IOrderService {

    private final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private OrderRepository orderRepository;

    @Override
    public List<OrderEntity> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<OrderEntity> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public void saveOrder(OrderDto message) {
        try {
            logger.info("saveOrder received message: {}", message);
            OrderEntity orderEntity = Mappers.toOrderEntity(message);
            orderRepository.save(orderEntity);
        } catch (Exception e) {
            logger.error("Error while saving order {}", message, e);
            throw e;
        }
    }
}
