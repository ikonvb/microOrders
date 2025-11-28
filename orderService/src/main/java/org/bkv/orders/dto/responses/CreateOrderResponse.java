package org.bkv.orders.dto.responses;

public record CreateOrderResponse(
        long orderId,
        boolean status
) {
}
