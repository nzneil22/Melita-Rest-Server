package com.melita_task.api.dao;

import com.melita_task.api.models.Client;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

@Data
@Service
@RequiredArgsConstructor
public class MySqlClientDao implements ClientDao {

    private final TransactionTemplate transactionTemplate;

    private final ClientRepository clientRepository;

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Client save(Client client) {
        clientRepository.save(client);
        return client;
    }

    @Override
    public Optional<Client> find(UUID clientId) {
        return transactionTemplate.execute(s -> {

            return clientRepository.findById(clientId);
        });
    }

}
