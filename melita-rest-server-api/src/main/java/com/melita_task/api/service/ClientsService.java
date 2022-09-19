package com.melita_task.api.service;

import com.melita_task.api.dao.ClientDao;
import com.melita_task.api.dao.OrderDao;
import com.melita_task.api.exceptions.ClientNotFoundException;
import com.melita_task.api.models.Client;
import com.melita_task.api.models.FullName;
import com.melita_task.api.models.InstallationAddress;
import com.melita_task.api.models.Orders;
import com.melita_task.contract.ClientDto;
import com.melita_task.contract.NewClientRequestDto;
import com.melita_task.contract.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientsService {

    private final MapperFacade mapper;

    private final ClientDao clientDao;

    private final OrderDao orderDao;

    public ClientDto registerClient(final NewClientRequestDto request) {
        Client c =  clientDao.save(mapper.map(request, Client.class));
        return mapper.map(c, ClientDto.class);
    }

    public Optional<Client> findClient(final String id) {
        return clientDao.find(id);
    }

    public ClientDto updateClient(final String id, final FullName fullName) {
        Client client =  clientDao.find(id).orElseThrow(() -> new ClientNotFoundException(id));
        client.updateClient(fullName);
        clientDao.save(client);
        return mapper.map(client, ClientDto.class);
    }
    public ClientDto updateClient(final String id, final InstallationAddress installationAddress) {
        Client client =  clientDao.find(id).orElseThrow(() -> new ClientNotFoundException(id));
        client.updateClient(installationAddress);
        return mapper.map(client, ClientDto.class);
    }

    public List<OrderDto> getClientOrders(final String uuid){
        return orderDao.find(uuid).stream().map(o -> mapper.map(o, OrderDto.class)).collect(Collectors.toList());
    }

    public OrderDto addOrder(final String id, final OrderDto service) {
        Orders ord = mapper.map(service, Orders.class);
        ord.setCustomerUUID(id);
        orderDao.save(ord);
        return mapper.map(ord, OrderDto.class);
    }

    public List<OrderDto> submitOrders(final String id) {
        List<Orders> ords = orderDao.find(id);

        ords.forEach(o -> {
            o.setStatus("Submitted");
            orderDao.save(o);
        });

        return orderDao.find(id).stream().map(o -> mapper.map(o, OrderDto.class)).collect(Collectors.toList());
    }
}
