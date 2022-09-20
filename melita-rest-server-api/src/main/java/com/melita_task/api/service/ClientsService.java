package com.melita_task.api.service;

import com.melita_task.api.amqp.MessagePayload;
import com.melita_task.api.amqp.MessageProducer;
import com.melita_task.api.dao.ClientDao;
import com.melita_task.api.dao.OrderDao;
import com.melita_task.api.exceptions.*;
import com.melita_task.api.models.*;
import com.melita_task.contract.ClientDto;
import com.melita_task.contract.NewClientRequestDto;
import com.melita_task.contract.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientsService {

    private final MapperFacade mapper;
    private final ClientDao clientDao;
    private final OrderDao orderDao;

    private final MessageProducer messageProducer;

    public ClientDto registerClient(final NewClientRequestDto request) {
        Client c =  clientDao.save(mapper.map(request, Client.class));
        messageProducer.sendMessage(MessagePayload.builder().client(c).alteration("Register New Client").build());
        return mapper.map(c, ClientDto.class);
    }

    public Optional<Client> findClient(final String id) {
        return clientDao.find(id);
    }

    public ClientDto updateClient(final String uuid, final FullNameUpdate fullName, final InstallationAddressUpdate installationAddress) {

        Client client = verifyClient(uuid);

        if(fullName != null) client.updateClient(fullName);
        if(installationAddress != null) client.updateClient(installationAddress);
        clientDao.save(client);
        messageProducer.sendMessage(MessagePayload.builder().client(client).alteration("Update Client").build());

        return mapper.map(client, ClientDto.class);
    }

    public List<OrderDto> getClientOrders(final String uuid){

        verifyClient(uuid);

        return orderDao.find(uuid).stream().map(o -> mapper.map(o, OrderDto.class)).collect(Collectors.toList());
    }

    public OrderDto addOrder(final String uuid, final OrderDto service) {
        Orders ord = mapper.map(service, Orders.class);

        verifyClient(uuid);

        ord.setId(UUID.randomUUID().toString());
        ord.setCustomerUUID(uuid);
        orderDao.save(ord);
        return mapper.map(ord, OrderDto.class);
    }

    public OrderDto editOrder(final String clientId, final String orderId, final OrdersUpdate oUpdate) {
        Orders ord = verifyOrder(clientId, orderId);

        ord.updateOrder(oUpdate);
        orderDao.save(ord);

        return mapper.map(ord, OrderDto.class);
    }

    public String cancelOrder(final String clientId, final String orderId) {
        Orders ord = verifyOrder(clientId, orderId);

        orderDao.delete(ord);

        return "Deleted Order with id: "+orderId;
    }

    public List<OrderDto> submitOrders(final String id) {
        List<Orders> ords = orderDao.find(id);

        ords.stream()
            .filter(o -> o.getStatus().equals("Created"))
            .forEach(o -> {
                o.setStatus("Submitted");
                orderDao.save(o);
                messageProducer.sendMessage(MessagePayload.builder().order(o).alteration("Submit Order").build());
            });

        return ords.stream().map(o -> mapper.map(o, OrderDto.class)).collect(Collectors.toList());
    }

    public ClientDto deactivateClient(final String clientId) {
        Client client = clientDao.find(clientId).orElseThrow(ClientNotFoundException::new);
        if(!client.getActive()) throw new ClientAlreadyDeActivatedException();

        client.setActive(false);
        clientDao.save(client);
        messageProducer.sendMessage(MessagePayload.builder().client(client).alteration("De-Activate Client").build());

        return mapper.map(client, ClientDto.class);
    }

    public ClientDto activateClient(final String clientId) {
        Client client = clientDao.find(clientId).orElseThrow(ClientNotFoundException::new);
        if(client.getActive()) throw new ClientAlreadyActivatedException();

        client.setActive(true);
        clientDao.save(client);
        messageProducer.sendMessage(MessagePayload.builder().client(client).alteration("Activate Client").build());

        return mapper.map(client, ClientDto.class);
    }

    public Client verifyClient(final String uuid){
        Client c = clientDao.find(uuid).orElseThrow(ClientNotFoundException::new);
        if(!c.getActive()) throw new ClientInactiveException();
        return c;
    }

    public Orders verifyOrder(final String clientId, final String orderId){
        Orders ord = orderDao.findByOrderId(orderId).orElseThrow(OrderNotFoundException::new);
        if(ord.getStatus().equals("Submitted")) throw new OrderSubmittedException();
        if(!ord.getCustomerUUID().equals(clientId)) throw new OrderNotOwnedException();
        return ord;
    }
}
