package com.melita_task.api.service;

import com.melita_task.api.amqp.MessageProducer;
import com.melita_task.api.dao.ClientDao;
import com.melita_task.api.exceptions.InvalidServiceIdException;
import com.melita_task.api.exceptions.OrderSubmittedException;
import com.melita_task.api.mapper.CustomMapper;
import com.melita_task.api.models.Client;
import com.melita_task.api.models.FullName;
import com.melita_task.api.models.InstallationAddress;
import com.melita_task.api.models.Order;
import com.melita_task.api.models.requests.CreateOrderRequest;
import com.melita_task.api.models.requests.UpdateOrderRequest;
import com.melita_task.contract.enums.LobTypes;
import com.melita_task.contract.enums.OrderStatus;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = OrderServiceTest.OrdersServiceTestConfig.class)
class OrderServiceTest {

    @MockBean
    private ClientDao clientDao;

    @MockBean
    private ProductCatalogService productCatalogService;

    @MockBean
    private MessageProducer messageProducer;

    @MockBean
    private ClientService clientService;

    @Autowired
    private OrderService sut;

    @Test
    void getClientOrders_incorrectClientId_ShouldThrowEntityNotFoundException() {

        Mockito.when(clientService.verifyClient(any(UUID.class))).thenThrow(EntityNotFoundException.class);

        final UUID clientId = UUID.randomUUID();
        Assertions.assertThatThrownBy(() -> sut.getClientOrders(clientId)).isInstanceOf(EntityNotFoundException.class);

        Mockito.verify(clientService, Mockito.times(1)).verifyClient(clientId);
    }

    @Test
    void getClientOrders_correctClientIdWithNoOrders_ShouldReturnAnEmptyList() {

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        Mockito.when(clientService.verifyClient(any(UUID.class))).thenReturn(client);

        final UUID clientId = UUID.randomUUID();
        Assertions.assertThat(sut.getClientOrders(clientId)).isNotNull().isEmpty();

        Mockito.verify(clientService, Mockito.times(1)).verifyClient(clientId);
    }

    @Test
    void getClientOrders_correctClientIdWithOrders_ShouldReturnAListOfOrders() {

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final CreateOrderRequest oRequest = new CreateOrderRequest(100, LobTypes.INT, new Date());

        client.addOrder(oRequest);

        Mockito.when(clientService.verifyClient(any(UUID.class))).thenReturn(client);

        final UUID clientId = UUID.randomUUID();
        Assertions.assertThat(sut.getClientOrders(clientId)).isNotEmpty();

        Mockito.verify(clientService, Mockito.times(1)).verifyClient(clientId);
    }

    @Test
    void addOrder_InCorrectClientId_ShouldThrowEntityNotFoundException() {

        final CreateOrderRequest oRequest = new CreateOrderRequest(100, LobTypes.INT, new Date());

        Mockito.when(clientService.verifyClient(any(UUID.class))).thenThrow(EntityNotFoundException.class);

        final UUID clientId = UUID.randomUUID();
        Assertions.assertThatThrownBy(() -> sut.addOrder(clientId, oRequest)).isInstanceOf(EntityNotFoundException.class);

        Mockito.verify(clientService, Mockito.times(1)).verifyClient(clientId);
    }

    @Test
    void addOrder_CorrectClientIdInvalidServiceIdInOrderRequest_ShouldThrowInvalidServiceIdException() {

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final int servId = 100;

        final CreateOrderRequest oRequest = new CreateOrderRequest(servId, LobTypes.INT, new Date());

        Mockito.when(clientService.verifyClient(any(UUID.class))).thenReturn(client);

        Mockito.when(productCatalogService.isServiceIdValid(any())).thenReturn(false);


        final UUID clientId = UUID.randomUUID();
        Assertions.assertThatThrownBy(() -> sut.addOrder(clientId, oRequest)).isInstanceOf(InvalidServiceIdException.class);

        Mockito.verify(clientService, Mockito.times(1)).verifyClient(clientId);
        Mockito.verify(productCatalogService, Mockito.times(1)).isServiceIdValid(servId);

    }

    @Test
    void addOrder_CorrectClientIdAndServiceIdInOrderRequest_ShouldAddOrderToClient() {

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final int servId = 100;

        final CreateOrderRequest oRequest = new CreateOrderRequest(servId, LobTypes.INT, new Date());

        Mockito.when(clientService.verifyClient(any(UUID.class))).thenReturn(client);

        Mockito.when(productCatalogService.isServiceIdValid(any())).thenReturn(true);


        final UUID clientId = UUID.randomUUID();
        Assertions.assertThat(sut.addOrder(clientId, oRequest)).isInstanceOf(Order.class);

        Mockito.verify(clientService, Mockito.times(1)).verifyClient(clientId);
        Mockito.verify(productCatalogService, Mockito.times(1)).isServiceIdValid(servId);

    }

    @Test
    void editOrder_InCorrectClientId_ShouldThrowEntityNotFoundException() {

        final UpdateOrderRequest oRequest = new UpdateOrderRequest(100, LobTypes.INT, new Date());

        Mockito.when(clientService.verifyClient(any(UUID.class))).thenThrow(EntityNotFoundException.class);

        final UUID clientId = UUID.randomUUID();
        Assertions.assertThatThrownBy(() -> sut.editOrder(clientId, UUID.randomUUID(), oRequest)).isInstanceOf(EntityNotFoundException.class);

        Mockito.verify(clientService, Mockito.times(1)).verifyClient(clientId);

    }

    @Test
    void editOrder_CorrectClientIdAndInvalidServiceIdInOrderRequest_ShouldThrowInvalidServiceIdException() {

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final int servId = 100;

        final UpdateOrderRequest oRequest = new UpdateOrderRequest(servId, LobTypes.INT, new Date());

        Mockito.when(clientService.verifyClient(any(UUID.class)))
                .thenReturn(client);

        Mockito.when(productCatalogService.isServiceIdValid(any()))
                .thenReturn(false);

        final UUID clientId = UUID.randomUUID();
        Assertions.assertThatThrownBy(() -> sut.editOrder(clientId, UUID.randomUUID(), oRequest))
                .isInstanceOf(InvalidServiceIdException.class);

        Mockito.verify(clientService, Mockito.times(1)).verifyClient(clientId);
        Mockito.verify(productCatalogService, Mockito.times(1)).isServiceIdValid(servId);

    }

    @Test
    void editOrder_CorrectClientIdAndValidServiceIdInOrderRequestAndOrderDoesNotBelongToClient_ShouldThrowIllegalStateException() {

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final int servId = 100;

        final UpdateOrderRequest oRequest = new UpdateOrderRequest(servId, LobTypes.INT, new Date());

        Mockito.when(clientService.verifyClient(any(UUID.class)))
                .thenReturn(client);

        Mockito.when(productCatalogService.isServiceIdValid(any()))
                .thenReturn(true);

        Mockito.when(clientDao.findOrderForUpdate(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());

        final UUID clientId = client.getId();
        final UUID orderId = UUID.randomUUID();

        Assertions.assertThatThrownBy(() -> sut.editOrder(clientId, orderId, oRequest))
                .isInstanceOf(IllegalStateException.class);

        Mockito.verify(clientService, Mockito.times(1)).verifyClient(clientId);
        Mockito.verify(productCatalogService, Mockito.times(1)).isServiceIdValid(servId);
        Mockito.verify(clientDao, Mockito.times(1)).findOrderForUpdate(clientId, orderId);

    }

    @Test
    void editOrder_CorrectClientIdAndValidServiceIdInOrderRequestAndOrderDoesBelongToClientButOrderIsSubmitted_ShouldThrowOrderSubmittedException() {

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final CreateOrderRequest oRequest = new CreateOrderRequest(200, LobTypes.INT, new Date());

        final Order order = client.addOrder(oRequest);

        order.setStatus(OrderStatus.SUBMITTED);

        final int servId = 100;

        final UpdateOrderRequest oUpdate = new UpdateOrderRequest(servId, LobTypes.MOB, new Date());

        Mockito.when(clientService.verifyClient(any(UUID.class)))
                .thenReturn(client);

        Mockito.when(productCatalogService.isServiceIdValid(any()))
                .thenReturn(true);

        Mockito.when(clientDao.findOrderForUpdate(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(order));

        final UUID clientId = client.getId();
        final UUID orderId = order.getId();

        Assertions.assertThatThrownBy(() -> sut.editOrder(clientId, orderId, oUpdate))
                .isInstanceOf(OrderSubmittedException.class);

        Mockito.verify(clientService, Mockito.times(1)).verifyClient(clientId);
        Mockito.verify(productCatalogService, Mockito.times(1)).isServiceIdValid(servId);
        Mockito.verify(clientDao, Mockito.times(1)).findOrderForUpdate(clientId, orderId);
    }

    @Test
    void editOrder_CorrectClientIdAndValidServiceIdInOrderRequestAndOrderDoesBelongToClientAndOrderNotSubmitted_ShouldReturnTheManipulatedOrder() {

        Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final CreateOrderRequest oRequest = new CreateOrderRequest(200, LobTypes.INT, new Date());

        final Order order = client.addOrder(oRequest);

        final int servId = 100;

        final UpdateOrderRequest oUpdate = new UpdateOrderRequest(servId, LobTypes.MOB, new Date());

        Mockito.when(clientService.verifyClient(any(UUID.class)))
                .thenReturn(client);

        Mockito.when(productCatalogService.isServiceIdValid(any()))
                .thenReturn(true);

        Mockito.when(clientDao.findOrderForUpdate(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(order));

        Mockito.when(clientDao.saveOrder(any(Order.class)))
                .thenReturn(order);

        final UUID clientId = client.getId();
        final UUID orderId = order.getId();

        Assertions.assertThat(sut.editOrder(clientId, orderId, oUpdate).getServiceId()).isEqualTo(servId);

        Mockito.verify(clientService, Mockito.times(1)).verifyClient(clientId);
        Mockito.verify(productCatalogService, Mockito.times(1)).isServiceIdValid(servId);
        Mockito.verify(clientDao, Mockito.times(1)).findOrderForUpdate(clientId, orderId);
        Mockito.verify(clientDao, Mockito.times(1)).saveOrder(order);

    }

    @Test
    void cancelOrder_IncorrectClientId_ShouldReturnEntityNotFoundException(){

        Mockito.when(clientService.verifyClient(any(UUID.class))).thenThrow(EntityNotFoundException.class);

        final UUID clientId = UUID.randomUUID();
        Assertions.assertThatThrownBy(() -> sut.cancelOrder(clientId, UUID.randomUUID())).isInstanceOf(EntityNotFoundException.class);

        Mockito.verify(clientService, Mockito.times(1)).verifyClient(clientId);

    }

    @Test
    void cancelOrder_CorrectClientIdButIncorrectOrderId_ShouldReturnIllegalStateException(){

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        Mockito.when(clientService.verifyClient(any(UUID.class))).thenReturn(client);

        Mockito.when(clientDao.findOrderForUpdate(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());

        final UUID clientId = client.getId();
        final UUID orderId = UUID.randomUUID();
        Assertions.assertThatThrownBy(() -> sut.cancelOrder(clientId, orderId))
                .isInstanceOf(IllegalStateException.class);

        Mockito.verify(clientService, Mockito.times(1)).verifyClient(clientId);
        Mockito.verify(clientDao, Mockito.times(1)).findOrderForUpdate(clientId, orderId);
    }

    @Test
    void cancelOrder_CorrectClientIdAndOrderIdButOrderIsAlreadySubmitted_ShouldReturnOrderSubmittedException(){

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final CreateOrderRequest oRequest = new CreateOrderRequest(200, LobTypes.INT, new Date());

        final Order order = client.addOrder(oRequest);

        order.setStatus(OrderStatus.SUBMITTED);

        Mockito.when(clientService.verifyClient(any(UUID.class))).thenReturn(client);

        Mockito.when(clientDao.findOrderForUpdate(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(order));

        final UUID clientId = client.getId();
        final UUID orderId = order.getId();
        Assertions.assertThatThrownBy(() -> sut.cancelOrder(clientId, orderId))
                .isInstanceOf(OrderSubmittedException.class);

        Mockito.verify(clientService, Mockito.times(1)).verifyClient(clientId);
        Mockito.verify(clientDao, Mockito.times(1)).findOrderForUpdate(clientId, orderId);

    }

    @Test
    void cancelOrder_CorrectClientIdAndOrderIdAndOrderIsNotSubmitted_ShouldDeleteOrderFromClient(){

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final CreateOrderRequest oRequest = new CreateOrderRequest(200, LobTypes.INT, new Date());

        final Order order = client.addOrder(oRequest);

        Mockito.when(clientService.verifyClient(any(UUID.class))).thenReturn(client);

        Mockito.when(clientDao.findOrderForUpdate(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(order));

        final UUID clientId = client.getId();
        final UUID orderId = order.getId();

        sut.cancelOrder(clientId, orderId);

        Assertions.assertThat(client.getOrders().contains(order)).isFalse();

        Mockito.verify(clientService, Mockito.times(1)).verifyClient(clientId);
        Mockito.verify(clientDao, Mockito.times(1)).findOrderForUpdate(clientId, orderId);

    }

    @Test
    void submitOrders_IncorrectClientId_ShouldReturnEntityNotFoundException(){
        Mockito.when(clientService.verifyClient(any(UUID.class))).thenThrow(EntityNotFoundException.class);

        final UUID clientId = UUID.randomUUID();
        Assertions.assertThatThrownBy(() -> sut.submitOrders(clientId)).isInstanceOf(EntityNotFoundException.class);

        Mockito.verify(clientService, Mockito.times(1)).verifyClient(clientId);
    }

    @Test
    void submitOrders_CorrectClientIdWithNoOrders_ShouldReturnAnEmptyList(){

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        Mockito.when(clientService.verifyClient(any(UUID.class))).thenReturn(client);

        final UUID clientId = client.getId();
        Assertions.assertThat(sut.submitOrders(clientId)).isEmpty();

        Mockito.verify(clientService, Mockito.times(1)).verifyClient(clientId);
    }

    @Test
    void submitOrders_CorrectClientIdWithNoOrders_ShouldReturnAListOfSubmittedOrders(){

        final Client client = new Client(new FullName("name", "helloworld", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final CreateOrderRequest oRequest1 = new CreateOrderRequest(200, LobTypes.INT, new Date());

        final Order order1 = client.addOrder(oRequest1);

        final CreateOrderRequest oRequest2 = new CreateOrderRequest(300, LobTypes.INT, new Date());

        final Order order2 = client.addOrder(oRequest2);

        Mockito.when(clientService.verifyClient(any(UUID.class))).thenReturn(client);

        final UUID clientId = client.getId();
        final List<Order> submittedOrders = sut.submitOrders(clientId);

        Assertions.assertThat(submittedOrders.contains(order1)).isTrue();
        Assertions.assertThat(submittedOrders.contains(order2)).isTrue();

        Mockito.verify(clientService, Mockito.times(1)).verifyClient(clientId);
    }



    public static class OrdersServiceTestConfig {

        @Bean
        public OrderService clientService(final MapperFacade mapper,
                                          final ClientDao clientDao,
                                          final ProductCatalogService productCatalogService,
                                          final MessageProducer messageProducer,
                                          final ClientService clientService) {

            return new OrderService(mapper,
                    clientDao,
                    productCatalogService,
                    messageProducer,
                    clientService);
        }

        @Bean
        public CustomMapper customMapper(){
            return new CustomMapper();
        }
    }
}