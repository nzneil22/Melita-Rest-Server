package com.melita_task.api.dao;

import com.melita_task.api.models.Client;

import java.util.Optional;
import java.util.UUID;

public interface ClientDao {

    Client save(Client client);

    Optional<Client> findClient(UUID clientId, boolean initialize);

    Optional<Client> findClientForUpdate(UUID clientId, boolean initialize);
}
