package com.melita_task.api.dao;

import com.melita_task.api.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {


    @Query("SELECT o FROM Order o where o.id =:id AND o.client.id =:clientId")
    Optional<Order> findById(@Param("id") UUID id, @Param("clientId") UUID clientId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o where o.id =:id AND o.client.id =:clientId")
    Optional<Order> findByIdAndClientIdForUpdate(@Param("id") UUID id, @Param("clientId") UUID clientId);
}
