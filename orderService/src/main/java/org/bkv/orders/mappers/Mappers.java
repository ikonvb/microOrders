package org.bkv.orders.mappers;

import org.bkv.orders.dto.requests.RegisterUserRequest;
import org.bkv.orders.entity.OrderEntity;
import org.bkv.orders.entity.UserEntity;
import org.bkv.orders.models.OrderDto;
import org.bkv.orders.models.UserDto;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Mappers {

    public static UserEntity toUserEntity(RegisterUserRequest registerUserRequest, PasswordEncoder passwordEncoder) {
        UserEntity userEntity = new UserEntity();
        userEntity.setRole(registerUserRequest.role());
        userEntity.setEmail(registerUserRequest.email());
        userEntity.setUserName(registerUserRequest.userName());
        userEntity.setPassword(passwordEncoder.encode(registerUserRequest.password()));
        return userEntity;
    }

    public static UserDto toUserDto(UserEntity savedUser) {
        return new UserDto(savedUser.getUserId(), savedUser.getUserName(), savedUser.getEmail(), savedUser.getRole());
    }

    public static OrderDto toOrderDto(OrderEntity savedOrder) {
        return new OrderDto(
                savedOrder.getOrderId(),
                savedOrder.getUserId(),
                savedOrder.getProductId(),
                savedOrder.getQuantity(),
                savedOrder.getPrice(),
                savedOrder.getTotalPrice());
    }
}
