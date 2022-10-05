package com.melita_task.api.dao;

import com.melita_task.api.models.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderDao {

    Optional<Order> findOrder(UUID orderId, UUID clientId);

    Optional<Order> findOrderForUpdate(UUID orderId, UUID clientId);

    Order saveOrder(Order order);
}
