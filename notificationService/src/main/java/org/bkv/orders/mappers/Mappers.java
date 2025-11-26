package org.bkv.orders.mappers;

import org.bkv.orders.entity.OrderEntity;
import org.bkv.orders.models.OrderDto;

public class Mappers {

    public static OrderEntity toOrderEntity(OrderDto orderDto) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(orderDto.orderId());
        orderEntity.setUserId(orderDto.userId());
        orderEntity.setProductId(orderDto.productId());
        orderEntity.setQuantity(orderDto.quantity());
        orderEntity.setPrice(orderDto.price());
        orderEntity.setTotalPrice(orderDto.totalPrice());
        return orderEntity;
    }
}
