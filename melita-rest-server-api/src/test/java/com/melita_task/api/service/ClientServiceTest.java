package com.melita_task.api.service;

import com.melita_task.api.amqp.MessageProducer;
import com.melita_task.api.dao.ClientDao;
import com.melita_task.api.exceptions.ClientAlreadyActivatedException;
import com.melita_task.api.exceptions.ClientAlreadyDeActivatedException;
import com.melita_task.api.exceptions.ClientInactiveException;
import com.melita_task.api.exceptions.ClientNotFoundException;
import com.melita_task.api.mapper.CustomMapper;
import com.melita_task.api.models.*;
import com.melita_task.api.models.requests.UpdateClientRequest;
import com.melita_task.contract.enums.ClientStatus;
import ma.glasnost.orika.MapperFacade;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ClientServiceTest.ClientsServiceTestConfig.class)
class ClientServiceTest {

    @MockBean
    private ClientDao clientDao;

    @MockBean
    private MessageProducer messageProducer;

    @Autowired
    private ClientService sut;

    @Test
    void findClient_clientIdNotResolved_shouldAttemptToLoadAndInitializeClientAndReturnEmptyOptional() {

        final UUID clientId = UUID.randomUUID();

        Mockito.when(clientDao.findClient(any(), anyBoolean())).thenReturn(Optional.empty());

        Assertions.assertThat(sut.findClient(clientId, true))
                .isNotNull()
                .isEmpty();

        Mockito.verify(clientDao, Mockito.times(1)).findClient(clientId, true);
    }

    @Test
    void findClient_clientIdCorrect_shouldLoadAndInitializeClientAndReturnOptionalContainingClientObject() {

        final Client client = new Client();

        final UUID clientId = UUID.randomUUID();

        Mockito.when(clientDao.findClient(any(), anyBoolean())).thenReturn(Optional.of(client));

        Assertions.assertThat(sut.findClient(clientId, true))
                .isNotNull()
                .isPresent();

        Mockito.verify(clientDao, Mockito.times(1)).findClient(clientId, true);
    }


    @Test
    void updateClient_clientIdIncorrect_shouldThrowEntityNotFoundException() {

        final UUID clientId = UUID.randomUUID();

        final UpdateClientRequest updateClientRequest = new UpdateClientRequest();

        Mockito.when(clientDao.findClientForUpdate(any(), anyBoolean())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> sut.updateClient(clientId, updateClientRequest))
                .isInstanceOf(EntityNotFoundException.class);

        Mockito.verify(clientDao, Mockito.times(1)).findClientForUpdate(clientId, true);

    }

    @Test
    void updateClient_clientIdCorrectAndDataWithinTheUpdateObject_shouldUpdateClientWithUpdateClientRequestObject() {

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final UpdateClientRequest updateClientRequest = new UpdateClientRequest(
                new FullNameUpdate(null, "middle", null),
                new InstallationAddressUpdate(null, "newTown", null, null));

        final UUID clientId = UUID.randomUUID();

        Mockito.when(clientDao.findClientForUpdate(any(), anyBoolean())).thenReturn(Optional.of(client));

        final Client newClient = sut.updateClient(clientId, updateClientRequest);


        Assertions.assertThat(newClient.getFullName().getMiddleName())
                .isEqualTo("middle");

        Assertions.assertThat(newClient.getInstallationAddress().getTown())
                .isEqualTo("newTown");

        Mockito.verify(clientDao, Mockito.times(1)).findClientForUpdate(clientId, true);

    }

    @Test
    void updateClient_clientIdCorrectAndEmptyUpdateObject_shouldReturnTheUnchangedClient() {

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final UUID clientId = UUID.randomUUID();

        final UpdateClientRequest updateClientRequest = new UpdateClientRequest(
                null,
                null);

        Mockito.when(clientDao.findClientForUpdate(any(), anyBoolean())).thenReturn(Optional.of(client));

        Assertions.assertThat(sut.updateClient(clientId, updateClientRequest))
                .isEqualTo(client);

        Mockito.verify(clientDao, Mockito.times(1)).findClientForUpdate(clientId, true);

    }

    @Test
    void clientStatus_clientIdIncorrect_shouldThrowEntityNotFoundException() {

        final UUID clientId = UUID.randomUUID();

        Mockito.when(clientDao.findClient(any(), anyBoolean())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> sut.clientStatus(clientId, ClientStatus.INACTIVE))
                .isInstanceOf(ClientNotFoundException.class);

        Mockito.verify(clientDao, Mockito.times(1)).findClient(clientId, false);

    }

    @Test
    void clientStatus_clientIdCorrectSameActiveStatus_shouldThrowClientAlreadyActivatedException() {

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final UUID clientId = UUID.randomUUID();

        Mockito.when(clientDao.findClient(any(), anyBoolean())).thenReturn(Optional.of(client));

        Assertions.assertThatThrownBy(() -> sut.clientStatus(clientId, ClientStatus.ACTIVE))
                .isInstanceOf(ClientAlreadyActivatedException.class);

        Mockito.verify(clientDao, Mockito.times(1)).findClient(clientId, false);

    }

    @Test
    void clientStatus_clientIdCorrectSameInActiveStatus_shouldThrowClientAlreadyDeActivatedException() {

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final UUID clientId = UUID.randomUUID();

        client.setStatus(ClientStatus.INACTIVE);

        Mockito.when(clientDao.findClient(any(), anyBoolean())).thenReturn(Optional.of(client));

        Assertions.assertThatThrownBy(() -> sut.clientStatus(clientId, ClientStatus.INACTIVE))
                .isInstanceOf(ClientAlreadyDeActivatedException.class);

        Mockito.verify(clientDao, Mockito.times(1)).findClient(clientId, false);

    }

    @Test
    void clientStatus_clientIdCorrectReActivate_shouldActivateTheClientInQuestion() {

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final UUID clientId = UUID.randomUUID();

        client.setStatus(ClientStatus.INACTIVE);

        Mockito.when(clientDao.findClient(any(), anyBoolean())).thenReturn(Optional.of(client));


        Assertions.assertThat(sut.clientStatus(clientId, ClientStatus.ACTIVE).getStatus())
                .isEqualTo(ClientStatus.ACTIVE);

        Mockito.verify(clientDao, Mockito.times(1)).findClient(clientId, false);

    }

    @Test
    void clientStatus_clientIdCorrectDeActivate_shouldDeActivateTheClientInQuestion() {

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final UUID clientId = UUID.randomUUID();


        Mockito.when(clientDao.findClient(any(), anyBoolean())).thenReturn(Optional.of(client));

        Assertions.assertThat(sut.clientStatus(clientId, ClientStatus.INACTIVE).getStatus())
                .isEqualTo(ClientStatus.INACTIVE);

        Mockito.verify(clientDao, Mockito.times(1)).findClient(clientId, false);

    }

    @Test
    void verifyClientForUpdate_clientIdInCorrect_shouldThrowEntityNoFoundException() {

        final UUID clientId = UUID.randomUUID();

        Mockito.when(clientDao.findClientForUpdate(any(), anyBoolean())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> sut.verifyClientForUpdate(clientId))
                .isInstanceOf(EntityNotFoundException.class);

        Mockito.verify(clientDao, Mockito.times(1)).findClientForUpdate(clientId, true);

    }

    @Test
    void verifyClientForUpdate_clientIdCorrectClientInactive_shouldThrowClientInactiveException() {

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final UUID clientId = UUID.randomUUID();

        client.setStatus(ClientStatus.INACTIVE);

        Mockito.when(clientDao.findClientForUpdate(any(), anyBoolean())).thenReturn(Optional.of(client));

        Assertions.assertThatThrownBy(() -> sut.verifyClientForUpdate(clientId))
                .isInstanceOf(ClientInactiveException.class);

        Mockito.verify(clientDao, Mockito.times(1)).findClientForUpdate(clientId, true);

    }

    @Test
    void verifyClientForUpdate_clientIdCorrectClientActive_shouldReturnTheClientObject() {

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final UUID clientId = UUID.randomUUID();

        Mockito.when(clientDao.findClientForUpdate(any(), anyBoolean())).thenReturn(Optional.of(client));

        Assertions.assertThat(sut.verifyClientForUpdate(clientId))
                .isEqualTo(client);

        Mockito.verify(clientDao, Mockito.times(1)).findClientForUpdate(clientId, true);

    }

    @Test
    void verifyClient_clientIdInCorrect_shouldThrowEntityNoFoundException() {

        final UUID clientId = UUID.randomUUID();

        Mockito.when(clientDao.findClient(any(), anyBoolean())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> sut.verifyClient(clientId, true))
                .isInstanceOf(EntityNotFoundException.class);

        Mockito.verify(clientDao, Mockito.times(1)).findClient(clientId, true);

    }

    @Test
    void verifyClient_clientIdCorrectClientInactive_shouldThrowClientInactiveException() {

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final UUID clientId = UUID.randomUUID();

        client.setStatus(ClientStatus.INACTIVE);

        Mockito.when(clientDao.findClient(any(), anyBoolean())).thenReturn(Optional.of(client));

        Assertions.assertThatThrownBy(() -> sut.verifyClient(clientId, true))
                .isInstanceOf(ClientInactiveException.class);

        Mockito.verify(clientDao, Mockito.times(1)).findClient(clientId, true);

    }

    @Test
    void verifyClient_clientIdCorrectClientActive_shouldReturnTheClientObject() {

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final UUID clientId = UUID.randomUUID();

        Mockito.when(clientDao.findClient(any(), anyBoolean())).thenReturn(Optional.of(client));

        Assertions.assertThat(sut.verifyClient(clientId, true))
                .isEqualTo(client);

        Mockito.verify(clientDao, Mockito.times(1)).findClient(clientId, true);

    }


    public static class ClientsServiceTestConfig {

        @Bean
        public ClientService clientService(final MapperFacade mapper,
                                           final ClientDao clientDao) {
            return new ClientService(mapper, clientDao);
        }

        @Bean
        public CustomMapper customMapper(){
            return new CustomMapper();
        }
    }


}