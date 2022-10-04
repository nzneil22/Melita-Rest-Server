package com.melita_task.api.dao;

import com.melita_task.api.models.Client;
import com.melita_task.api.models.Order;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.util.UUID;

@Data
@Slf4j
@Service
@Profile("!in-memory")
@RequiredArgsConstructor
public class MySqlClientDao implements ClientDao {

    private final TransactionTemplate transactionTemplate;

    private final ClientRepository clientRepository;

    private final OrderRepository orderRepository;

    @Override
    public Client save(Client client) {
        clientRepository.save(client);
        return client;
    }

    @Override
    public Optional<Client> findClient(UUID clientId, boolean initialize) {

        Optional<Client> c = clientRepository.findById(clientId);

        if(initialize && c.isPresent()) Hibernate.initialize(c.get().getOrders());

        return c;

    }

    @Override
    public Optional<Client> findClientForUpdate(UUID clientId, boolean initialize) {
        Optional<Client> c = clientRepository.findByIdForUpdate(clientId);

        if(initialize && c.isPresent()) Hibernate.initialize(c.get().getOrders());

        return c;
    }

    @Override
    public Optional<Order> findOrder(UUID clientId, UUID orderId) {
        return orderRepository.findById(clientId, orderId);
    }

    @Override
    public Optional<Order> findOrderForUpdate(UUID clientId, UUID orderId) {
        return orderRepository.findByIdAndClientIdForUpdate(clientId, orderId);
    }


    @Override
    public Order saveOrder(Order order) {
        orderRepository.save(order);
        return order;
    }


}
