package org.bkv.orders.services.interfaces;

import org.bkv.orders.dto.requests.RegisterUserRequest;
import org.bkv.orders.entity.UserEntity;
import org.bkv.orders.models.LoginResult;

import java.util.Optional;

public interface IUserService {

    LoginResult login(String username, String password);
    UserEntity saveUser(UserEntity user);

    Optional<UserEntity> findByUserName(String userName);

    UserEntity saveAndGetUserEntity(RegisterUserRequest registerUser);

    boolean checkRefreshToken(String refreshToken);

    String createRefreshToken(String refreshToken);
}
