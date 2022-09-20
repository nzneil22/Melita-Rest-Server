package com.melita_task.api.dao;

import com.melita_task.api.models.Orders;

import java.util.List;
import java.util.Optional;

public interface OrderDao {

    Orders save(Orders order);

    List<Orders> find(String id);
    Optional<Orders> findByOrderId(String id);

    void delete(Orders order);

}
