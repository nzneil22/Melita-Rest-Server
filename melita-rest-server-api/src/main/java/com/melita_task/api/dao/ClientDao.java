package com.melita_task.api.dao;

import com.melita_task.api.models.Client;

import java.util.Optional;

public interface ClientDao {

    Client save(Client client);

    Optional<Client> find(String id);
}
