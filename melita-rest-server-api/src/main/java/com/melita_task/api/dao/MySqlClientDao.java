package com.melita_task.api.dao;

import com.melita_task.api.models.Client;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Data
@Service
@RequiredArgsConstructor
public class MySqlClientDao implements ClientDao {

    private final ClientRepository clientRepository;

    @Override
    public Client save(Client client) {
        clientRepository.save(client);
        return client;
    }

    @Override
    public Optional<Client> find(UUID id) {
        return clientRepository.findAllById(id);
    }

}
