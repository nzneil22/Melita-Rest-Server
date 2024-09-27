package com.melita_task.api.dao;

import com.melita_task.api.models.Client;
import com.melita_task.api.models.FullName;
import com.melita_task.api.models.InstallationAddress;
import com.melita_task.api.models.Order;
import com.melita_task.api.models.requests.CreateOrderRequest;
import com.melita_task.contract.enums.LobTypes;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j(topic = "DEBUG")
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("classpath:application.yml")
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {MySqlClientDao.class, ClientRepository.class, OrderRepository.class}))
class MySqlClientDaoTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private MySqlClientDao sut;

    @Test
    void save_whenGivenClientEntity_shouldTakeClientAndSaveItToDatabase() {
        final Client client = new Client(new FullName("firstName", "middleName", "lastName"),
                new InstallationAddress("island", "town", "street", "building"));

        Assertions.assertThat(sut.save(client)).isEqualTo(client);
        Assertions.assertThat(testEntityManager.find(Client.class, client.getId()).getId()).isEqualTo(client.getId());

    }

    @Test
    void saveOrder_whenGivenOrderEntity_shouldTakeOrderAndSaveItToDatabase() throws ParseException {
        final Client client = new Client(new FullName("firstName", "middleName", "lastName"),
                new InstallationAddress("island", "town", "street", "building"));

        final String sDate = "31-Dec-2050 23:37:50";
        final Date date = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(sDate);

        final CreateOrderRequest createOrderRequest = new CreateOrderRequest(100, LobTypes.INT, date);

        final Order order = new Order(client, createOrderRequest);

        Assertions.assertThat(sut.saveOrder(order)).isEqualTo(order);
        Assertions.assertThat(testEntityManager.find(Order.class, order.getId()).getId()).isEqualTo(order.getId());
        Assertions.assertThat(testEntityManager.find(Order.class, order.getId()).getClient().getId()).isEqualTo(client.getId());
    }

    @Test
    void findClient_whenGivenExistingClientUUID_shouldReturnOptionalContainingClient() {
        final Client client = new Client(new FullName("firstName", "middleName", "lastName"),
                new InstallationAddress("island", "town", "street", "building"));

        testEntityManager.persistAndFlush(client);

        final Optional<Client> returned = sut.findClient(client.getId(), true);

        log.info("Returned: <<{}>>", returned);

        Assertions.assertThat(returned)
                .isInstanceOf(Optional.class)
                .isPresent();
    }

    @Test
    void findClient_whenGivenNonExistingClientUUID_shouldReturnEmptyOptional() {
        Assertions.assertThat(sut.findClient(UUID.randomUUID(), false))
                .isInstanceOf(Optional.class)
                .isEmpty();
    }

    @Test
    void findClient_whenGivenExistingClientUUIDwithOrders_shouldReturnClientWithOrders() throws ParseException {
        final Client client = new Client(new FullName("firstName", "middleName", "lastName"),
                new InstallationAddress("island", "town", "street", "building"));


        final String sDate = "31-Dec-2050 23:37:50";
        final Date date = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(sDate);
        final CreateOrderRequest createOrderRequest = new CreateOrderRequest(100, LobTypes.INT, date);

        client.addOrder(createOrderRequest);

        testEntityManager.persistAndFlush(client);

        List<Order> returned = sut.findClient(client.getId(), true).get().getOrders();

        log.info("Returned: <<{}>>", returned);

        Assertions.assertThat(returned).isNotEmpty();

    }

    @Test
    void findClientForUpdate_whenGivenExistingClientUUID_shouldReturnOptionalContainingClient() {
        final Client client = new Client(new FullName("firstName", "middleName", "lastName"),
                new InstallationAddress("island", "town", "street", "building"));

        testEntityManager.persistAndFlush(client);

        final Optional<Client> returned = sut.findClientForUpdate(client.getId(), true);

        log.info("Returned: <<{}>>", returned);

        Assertions.assertThat(returned)
                .isInstanceOf(Optional.class)
                .isPresent();
    }

    @Test
    void findClientForUpdate_whenGivenNonExistingClientUUID_shouldReturnEmptyOptional() {
        Assertions.assertThat(sut.findClientForUpdate(UUID.randomUUID(), false))
                .isInstanceOf(Optional.class)
                .isEmpty();
    }

    @Test
    void findClientForUpdate_whenGivenExistingClientUUID_shouldReturnClient() throws ParseException {
        final Client client = new Client(new FullName("firstName", "middleName", "lastName"),
                new InstallationAddress("island", "town", "street", "building"));


        final String sDate = "31-Dec-2050 23:37:50";
        final Date date = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(sDate);
        final CreateOrderRequest createOrderRequest = new CreateOrderRequest(100, LobTypes.INT, date);

        client.addOrder(createOrderRequest);

        testEntityManager.persistAndFlush(client);

        List<Order> returned = sut.findClientForUpdate(client.getId(), true).get().getOrders();

        log.info("Returned: <<{}>>", returned);

        Assertions.assertThat(returned).isNotEmpty();

    }

    @Test
    void findOrder_whenGivenExistingClientUUIDAndCorrectOrderUUID_shouldReturnOptionalContainingTheDesiredOrder() throws ParseException {
        final Client client = new Client(new FullName("firstName", "middleName", "lastName"),
                new InstallationAddress("island", "town", "street", "building"));

        final String sDate = "31-Dec-2050 23:37:50";
        final Date date = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(sDate);
        final CreateOrderRequest createOrderRequest = new CreateOrderRequest(100, LobTypes.INT, date);

        client.addOrder(createOrderRequest);

        final Order order = client.getOrders().get(0);

        testEntityManager.persistAndFlush(client);

        final Optional<Order> returned = sut.findOrder(order.getId(), client.getId());

        log.info("Returned: <<{}>>", returned);

        Assertions.assertThat(returned)
                .isInstanceOf(Optional.class)
                .isPresent()
                .get()
                .isEqualTo(order);
    }

    @Test
    void findOrder_whenGivenExistingClientUUIDAndOrderUUIDDoesNotBelongToClient_shouldReturnEmptyOptional() throws ParseException {
        final Client client = new Client(new FullName("firstName", "middleName", "lastName"),
                new InstallationAddress("island", "town", "street", "building"));

        final String sDate = "31-Dec-2050 23:37:50";
        final Date date = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(sDate);
        final CreateOrderRequest createOrderRequest = new CreateOrderRequest(100, LobTypes.INT, date);

        client.addOrder(createOrderRequest);

        testEntityManager.persistAndFlush(client);

        final Optional<Order> returned = sut.findOrder(UUID.randomUUID(), client.getId());

        log.info("Returned: <<{}>>", returned);

        Assertions.assertThat(returned)
                .isInstanceOf(Optional.class)
                .isEmpty();
    }

    @Test
    void findOrderForUpdate_whenGivenExistingClientUUIDAndCorrectOrderUUID_shouldReturnOptionalContainingTheDesiredOrder() throws ParseException {
        final Client client = new Client(new FullName("firstName", "middleName", "lastName"),
                new InstallationAddress("island", "town", "street", "building"));

        final String sDate = "31-Dec-2050 23:37:50";
        final Date date = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(sDate);
        final CreateOrderRequest createOrderRequest = new CreateOrderRequest(100, LobTypes.INT, date);

        client.addOrder(createOrderRequest);

        final Order order = client.getOrders().get(0);

        testEntityManager.persistAndFlush(client);

        final Optional<Order> returned = sut.findOrderForUpdate(order.getId(), client.getId());

        log.info("Returned: <<{}>>", returned);

        Assertions.assertThat(returned)
                .isInstanceOf(Optional.class)
                .isPresent()
                .get()
                .isEqualTo(order);
    }

    @Test
    void findOrderForUpdate_whenGivenExistingClientUUIDAndOrderUUIDDoesNotBelongToClient_shouldReturnEmptyOptional() throws ParseException {
        final Client client = new Client(new FullName("firstName", "middleName", "lastName"),
                new InstallationAddress("island", "town", "street", "building"));

        final String sDate = "31-Dec-2050 23:37:50";
        final Date date = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(sDate);
        final CreateOrderRequest createOrderRequest = new CreateOrderRequest(100, LobTypes.INT, date);

        client.addOrder(createOrderRequest);

        testEntityManager.persistAndFlush(client);

        final Optional<Order> returned = sut.findOrderForUpdate(UUID.randomUUID(), client.getId());

        log.info("Returned: <<{}>>", returned);

        Assertions.assertThat(returned)
                .isInstanceOf(Optional.class)
                .isEmpty();
    }






}