package org.bkv.orders.dto.requests;

public record CreateOrderRequest(
        Long userId,
        Long productId,
        Integer quantity
) {
}