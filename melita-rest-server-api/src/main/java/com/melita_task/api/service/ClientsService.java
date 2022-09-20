package com.melita_task.api.service;

import com.melita_task.api.amqp.MessagePayload;
import com.melita_task.api.amqp.MessageProducer;
import com.melita_task.api.dao.ClientDao;
import com.melita_task.api.exceptions.*;
import com.melita_task.api.models.*;
import com.melita_task.contract.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
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

    private final MessageProducer messageProducer;

    public ClientDto registerClient(final NewClientRequestDto request) {
        Client client =  clientDao.save(mapper.map(request, Client.class));
        messageProducer.sendMessage(
                MessagePayload.builder()
                        .client(client)
                        .alteration("Register New Client")
                        .build());
        return mapper.map(client, ClientDto.class);
    }

    public Optional<Client> findClient(final UUID id) {
        return clientDao.find(id);
    }

    public ClientDto updateClient(final UUID clientId,
                                  final FullNameUpdate fullName,
                                  final InstallationAddressUpdate installationAddress) {

        Client client = verifyClient(clientId);

        if(fullName != null) client.updateClient(fullName);
        if(installationAddress != null) client.updateClient(installationAddress);

        clientDao.save(client);
        messageProducer.sendMessage(
                MessagePayload.builder()
                        .client(client)
                        .alteration("Update Client")
                        .build());

        return mapper.map(client, ClientDto.class);
    }

    public List<OrderDto> getClientOrders(final UUID clientId){

        Client c = verifyClient(clientId);

        return c.getOrders()
                .stream()
                .map(o -> mapper.map(o, OrderDto.class))
                .collect(Collectors.toList());
    }

    public OrderDto addOrder(final UUID clientId, final OrderDto ordDto) {

        Client client = verifyClient(clientId);

        Order ord = new Order(
                ordDto.getServiceId(),
                ordDto.getLobType(),
                ordDto.getInstallationDateAndTime(),
                OrderStatus.CREATED);

        client.getOrders().add(ord);

        clientDao.save(client);

        return mapper.map(ord, OrderDto.class);
    }

    public OrderDto editOrder(final UUID clientId, final UUID orderId, final OrdersUpdate oUpdate) {

        Client client = verifyClient(clientId);

        Order ord = client.getOrders().stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst()
                .orElseThrow(EntityNotFoundException::new);

        if(ord.getStatus().equals(OrderStatus.SUBMITTED)) throw new OrderSubmittedException();

        ord.updateOrder(oUpdate);

        clientDao.save(client);

        return mapper.map(ord, OrderDto.class);
    }

    public String cancelOrder(final UUID clientId, final UUID orderId) {

        Client client = verifyClient(clientId);

        Order ord = client.getOrders()
                .stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst()
                .orElseThrow(EntityNotFoundException::new);

        if(ord.getStatus().equals(OrderStatus.SUBMITTED))throw new OrderSubmittedException();

        client.getOrders().remove(ord);

        clientDao.save(client);

        return "Deleted Order with id: "+orderId;
    }

    public List<OrderDto> submitOrders(final UUID clientId) {

        Client client = verifyClient(clientId);

        client.getOrders().stream()
            .filter(o -> o.getStatus().equals(OrderStatus.CREATED))
            .forEach(o -> o.setStatus(OrderStatus.SUBMITTED));

        messageProducer.sendMessage(
                MessagePayload.builder()
                        .client(client)
                        .alteration("Submit Orders")
                        .build());

        clientDao.save(client);

        return client.getOrders().stream().map(o -> mapper.map(o, OrderDto.class)).collect(Collectors.toList());
    }

    public ClientDto clientStatus(final UUID clientId, final ClientStatus cStatus){
        Client client = clientDao.find(clientId).orElseThrow(ClientNotFoundException::new);

        if(client.getStatus().equals(ClientStatus.INACTIVE)
                && cStatus.equals(ClientStatus.INACTIVE)){
            throw new ClientAlreadyDeActivatedException();
        } else if(client.getStatus().equals(ClientStatus.ACTIVE)
                && cStatus.equals(ClientStatus.ACTIVE)){
            throw new ClientAlreadyActivatedException();
        }


        client.setStatus(cStatus);
        clientDao.save(client);

        messageProducer.sendMessage(
                MessagePayload.builder()
                        .client(client)
                        .alteration("Client Status Change")
                        .build());

        return mapper.map(client, ClientDto.class);
    }

    public Client verifyClient(final UUID clientId){
        Client client = clientDao.find(clientId).orElseThrow(EntityNotFoundException::new);
        if(client.getStatus().equals(ClientStatus.INACTIVE)) throw new ClientInactiveException();
        return client;
    }
}
