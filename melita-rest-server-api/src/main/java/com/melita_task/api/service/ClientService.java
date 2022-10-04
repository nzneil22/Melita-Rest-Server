package com.melita_task.api.service;

import com.melita_task.api.amqp.MessagePayload;
import com.melita_task.api.amqp.MessageProducer;
import com.melita_task.api.dao.ClientDao;
import com.melita_task.api.exceptions.ClientAlreadyActivatedException;
import com.melita_task.api.exceptions.ClientAlreadyDeActivatedException;
import com.melita_task.api.exceptions.ClientInactiveException;
import com.melita_task.api.exceptions.ClientNotFoundException;
import com.melita_task.api.models.Client;
import com.melita_task.api.models.FullName;
import com.melita_task.api.models.InstallationAddress;
import com.melita_task.api.models.requests.CreateClientRequest;
import com.melita_task.api.models.requests.UpdateClientRequest;
import com.melita_task.contract.ClientDtoRabbit;
import com.melita_task.contract.enums.ClientStatus;
import com.melita_task.contract.enums.EventTypes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ClientService {

    private final MapperFacade mapper;
    private final ClientDao clientDao;
    private final MessageProducer messageProducer;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Client registerClient(final CreateClientRequest request) {

        final Client newClient = new Client(mapper.map(request.getFullName(), FullName.class),
                mapper.map(request.getInstallationAddress(), InstallationAddress.class));

        clientDao.save(newClient);

        messageProducer.sendMessage(
                MessagePayload.builder()
                        .client(mapper.map(newClient, ClientDtoRabbit.class))
                        .event(EventTypes.CLIENT_CREATED)
                        .build());

        log.info("Registered New Client");

        return newClient;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Optional<Client> findClient(final UUID id) {

        final Optional<Client> client = clientDao.findClient(id, true);
        client.ifPresent(value -> log.debug("Client Id [{}] resolved to client [{}]", id, value));
        return client;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Client updateClient(final UUID clientId, final UpdateClientRequest request) {

        final Client client = verifyClientForUpdate(clientId);

        client.update(request);

        clientDao.save(client);

        messageProducer.sendMessage(
                MessagePayload.builder()
                        .client(mapper.map(client, ClientDtoRabbit.class))
                        .event(EventTypes.CLIENT_UPDATED)
                        .build());
        return client;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Client clientStatus(final UUID clientId, final ClientStatus cStatus) {
        Client client = clientDao.findClient(clientId, false).orElseThrow(ClientNotFoundException::new);

        if (client.getStatus().equals(ClientStatus.INACTIVE)
                && cStatus.equals(ClientStatus.INACTIVE)) {
            throw new ClientAlreadyDeActivatedException();
        } else if (client.getStatus().equals(ClientStatus.ACTIVE)
                && cStatus.equals(ClientStatus.ACTIVE)) {
            throw new ClientAlreadyActivatedException();
        }

        client.setStatus(cStatus);
        clientDao.save(client);

        messageProducer.sendMessage(
                MessagePayload.builder()
                        .client(mapper.map(client, ClientDtoRabbit.class))
                        .event(EventTypes.CLIENT_UPDATED)
                        .build());

        return client;
    }

    public Client verifyClientForUpdate(final UUID clientId) {
        final Client client = clientDao.findClientForUpdate(clientId, false).orElseThrow(EntityNotFoundException::new);

        if (!client.isActive()) {
            log.error("Client [{}] is currently inactive", clientId);
            throw new ClientInactiveException();
        }

        return client;
    }

    public Client verifyClient(final UUID clientId) {

        final Client client = clientDao.findClient(clientId, false).orElseThrow(EntityNotFoundException::new);

        if (!client.isActive()) {
            log.error("Client [{}] is currently inactive", clientId);
            throw new ClientInactiveException();
        };

        return client;
    }

}
