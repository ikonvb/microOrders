package org.bkv.orders.dto.responses;

import org.bkv.orders.models.UserDto;

public record LoginUserResponse(
        UserDto user,
        String accessToken,
        boolean status
) {
}