package com.melita_task.api.dao;

import com.melita_task.api.models.Orders;

import java.util.List;

public interface OrderDao {

    Orders save(Orders client);

    List<Orders> find(String id);
}
