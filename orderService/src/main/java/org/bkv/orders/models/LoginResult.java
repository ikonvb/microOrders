package org.bkv.orders.models;

public record LoginResult(UserDto user, String accessToken, String refreshToken) {}