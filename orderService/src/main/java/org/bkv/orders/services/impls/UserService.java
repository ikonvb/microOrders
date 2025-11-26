package org.bkv.orders.services.impls;

import lombok.AllArgsConstructor;
import org.bkv.orders.entity.UserEntity;
import org.bkv.orders.repo.UserRepository;
import org.bkv.orders.services.interfaces.IUserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements IUserService {


    private UserRepository userRepository;

    @Override
    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<UserEntity> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }
}
