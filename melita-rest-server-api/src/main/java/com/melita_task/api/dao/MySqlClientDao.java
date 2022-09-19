package com.melita_task.api.dao;

import com.melita_task.api.models.Client;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
    public Optional<Client> find(String id) {
        return clientRepository.findById(id);
    }
}
