package org.bkv.orders.mappers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.bkv.orders.dto.requests.CreateOrderRequest;
import org.bkv.orders.entity.OrderEntity;
import org.bkv.orders.models.OrderDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDto toOrderDto(OrderEntity entity);

    OrderEntity toOrderEntity(@Valid @NotNull CreateOrderRequest request);
}
