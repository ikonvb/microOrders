package org.bkv.orders.services.interfaces;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.antlr.v4.runtime.misc.Pair;
import org.bkv.orders.dto.requests.CreateOrderRequest;
import org.bkv.orders.entity.OrderEntity;
import org.bkv.orders.models.OrderDto;

public interface IOrderService {
    OrderEntity saveOrder(OrderEntity order);

    Pair<Boolean, Double> canCreateOrder(OrderEntity order);

    OrderEntity getOrderEntity(@Valid @NotNull CreateOrderRequest request);

    OrderDto getOderDto(OrderEntity savedOrder);


}
