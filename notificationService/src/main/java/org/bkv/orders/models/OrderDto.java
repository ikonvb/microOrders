package org.bkv.orders.models;

public record OrderDto(
        Long orderId,
        Long userId,
        Long productId,
        int quantity,
        Long price,
        Long totalPrice
) {
}
