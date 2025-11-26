package org.bkv.orders.repo;

import lombok.NonNull;
import org.bkv.orders.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<@NonNull UserEntity, @NonNull Long> {

    Optional<UserEntity> findByUserName(String userName);

}
