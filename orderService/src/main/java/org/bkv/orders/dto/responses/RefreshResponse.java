package org.bkv.orders.dto.responses;

public record RefreshResponse(
        String token,
        boolean status
) {
}