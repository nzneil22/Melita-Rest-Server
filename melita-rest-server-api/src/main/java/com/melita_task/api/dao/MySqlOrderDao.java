package com.melita_task.api.dao;

import com.melita_task.api.models.Orders;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

@Data
@Service
@RequiredArgsConstructor
public class MySqlOrderDao implements OrderDao {

    private final OrderRepository orderRepository;

    @Override
    public Orders save(Orders ord) {
        if(isNull(ord.getStatus())) ord.setStatus("Created");
        orderRepository.save(ord);
        return ord;
    }

    @Override
    public List<Orders> find(String id) {
        return orderRepository.findAllByCustomerUUID(id);
    }

    @Override
    public Optional<Orders> findByOrderId(String id) {
        return orderRepository.findAllById(id);
    }

    @Override
    public void delete(Orders order) {
        orderRepository.delete(order);
    }

}
