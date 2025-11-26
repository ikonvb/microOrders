package org.bkv.orders.repo;

import lombok.NonNull;
import org.bkv.orders.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<@NonNull ProductEntity, @NonNull Long> {
}
