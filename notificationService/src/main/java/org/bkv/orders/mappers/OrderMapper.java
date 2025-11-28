package org.bkv.orders.mappers;

import org.bkv.orders.entity.OrderEntity;
import org.bkv.orders.models.OrderDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderEntity toOrderEntity(OrderDto orderDto);
}
