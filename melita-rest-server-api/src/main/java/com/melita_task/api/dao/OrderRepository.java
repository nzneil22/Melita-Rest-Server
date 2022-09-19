package com.melita_task.api.dao;

import com.melita_task.api.models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository  extends JpaRepository<Orders, String> {
    List<Orders> findAllByCustomerUUID(String id);
}
