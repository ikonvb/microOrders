package org.bkv.orders.mappers;

import org.bkv.orders.dto.requests.RegisterUserRequest;
import org.bkv.orders.entity.UserEntity;
import org.bkv.orders.models.UserDto;
import org.mapstruct.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    UserEntity toUserEntity(RegisterUserRequest request);

    UserDto toUserDto(UserEntity entity);

    @AfterMapping
    default void encodePassword(
            @MappingTarget UserEntity user,
            RegisterUserRequest request,
            @Context PasswordEncoder passwordEncoder
    ) {
        user.setPassword(passwordEncoder.encode(request.password()));
    }

    UserEntity toUserEntity(
            RegisterUserRequest request,
            @Context PasswordEncoder passwordEncoder
    );
}
