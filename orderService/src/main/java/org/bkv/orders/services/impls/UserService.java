package org.bkv.orders.services.impls;

import lombok.AllArgsConstructor;
import org.bkv.orders.dto.requests.RegisterUserRequest;
import org.bkv.orders.entity.UserEntity;
import org.bkv.orders.mappers.UserMapper;
import org.bkv.orders.models.LoginResult;
import org.bkv.orders.models.UserDto;
import org.bkv.orders.repo.UserRepository;
import org.bkv.orders.security.JwtUtil;
import org.bkv.orders.services.interfaces.IUserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResult login(String username, String password) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        UserDto user = userRepository.findByUserName(username)
                .map(userMapper::toUserDto)
                .orElse(null);

        return new LoginResult(user, accessToken, refreshToken);
    }

    @Override
    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<UserEntity> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public UserEntity saveAndGetUserEntity(RegisterUserRequest registerUser) {

        if (isUserPresent(registerUser)) {
            return null;
        }

        UserEntity entity = userMapper.toUserEntity(registerUser, passwordEncoder);
        return userRepository.save(entity);
    }

    @Override
    public boolean checkRefreshToken(String refreshToken) {
        return jwtUtil.isRefreshTokenValid(refreshToken);
    }

    @Override
    public String createRefreshToken(String refreshToken) {
        String username = jwtUtil.extractUserName(refreshToken);
        return jwtUtil.generateAccessToken(username);
    }

    private boolean isUserPresent(RegisterUserRequest registerUser) {
        return findByUserName(registerUser.userName()).isPresent();
    }
}
