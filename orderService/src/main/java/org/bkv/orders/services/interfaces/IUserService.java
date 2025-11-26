package org.bkv.orders.services.interfaces;

import org.bkv.orders.entity.UserEntity;

import java.util.Optional;

public interface IUserService {

    UserEntity saveUser(UserEntity user);

    Optional<UserEntity> findByUserName(String userName);
}
