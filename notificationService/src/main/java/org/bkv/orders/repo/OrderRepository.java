package org.bkv.orders.repo;

import lombok.NonNull;
import org.bkv.orders.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<@NonNull OrderEntity, @NonNull Long> {
}
