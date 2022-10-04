package com.melita_task.api.service;

import com.melita_task.api.amqp.MessagePayload;
import com.melita_task.api.amqp.MessageProducer;
import com.melita_task.api.dao.ClientDao;
import com.melita_task.api.exceptions.InvalidServiceIdException;
import com.melita_task.api.exceptions.OrderSubmittedException;
import com.melita_task.api.models.Client;
import com.melita_task.api.models.Order;
import com.melita_task.api.models.requests.CreateOrderRequest;
import com.melita_task.api.models.requests.UpdateOrderRequest;
import com.melita_task.contract.ClientDtoRabbit;
import com.melita_task.contract.enums.EventTypes;
import com.melita_task.contract.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.hibernate.Hibernate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final MapperFacade mapper;
    private final ClientDao clientDao;
    private final ProductCatalogService productCatalogService;
    private final MessageProducer messageProducer;
    private final ClientService clientService;


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<Order> getClientOrders(final UUID clientId) throws EntityNotFoundException{

        final Client c = clientService.verifyClient(clientId);

        return c.getOrders();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Order addOrder(final UUID clientId, final CreateOrderRequest request) {

        final Client client = clientService.verifyClient(clientId);

        if (!productCatalogService.isServiceIdValid(request.getServiceId())){
            log.error("Service id [{}] is not found within the product catalog", request.getServiceId());
            throw new InvalidServiceIdException();
        }

        final Order order = client.addOrder(request);
        clientDao.save(client);

        return order;
    }



    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Order editOrder(final UUID clientId, final UUID orderId, final UpdateOrderRequest oUpdate) {

        final Client client = clientService.verifyClient(clientId);

        if(oUpdate.getServiceId().isPresent()){
            if(!productCatalogService.isServiceIdValid(oUpdate.getServiceId().get())) {
                log.error("Service id [{}] is not found within the product catalog", oUpdate.getServiceId().get());
                throw new InvalidServiceIdException();
            }
        }

        final Order order = verifyOrder(client, orderId);

        if (order.getStatus().equals(OrderStatus.SUBMITTED)) {
            log.error("Order [{}] is already submitted and hence cannot be edited", orderId);
            throw new OrderSubmittedException();
        }


        log.debug("Order with ID [{}] for client with ID [{}] resolved to [{}]", orderId, clientId, order);

        order.updateOrder(oUpdate);

        return clientDao.saveOrder(order);

    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public String cancelOrder(final UUID clientId, final UUID orderId) {

        final Client client = clientService.verifyClient(clientId);

        final Order order = verifyOrder(client, orderId);

        if (order.getStatus().equals(OrderStatus.SUBMITTED)) {
            log.error("Order [{}] is already submitted and hence cannot be cancelled", orderId);
            throw new OrderSubmittedException();
        }

        client.cancelOrder(order);

        clientDao.save(client);

        return "Deleted Order with id: " + orderId;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<Order> submitOrders(final UUID clientId) {

        final Client client = clientService.verifyClient(clientId);

        client.getOrders().stream()
                .filter(o -> o.getStatus().equals(OrderStatus.CREATED))
                .forEach(o -> o.setStatus(OrderStatus.SUBMITTED));

        messageProducer.sendMessage(
                MessagePayload.builder()
                        .client(mapper.map(client, ClientDtoRabbit.class))
                        .event(EventTypes.ORDER_SUBMIT)
                        .build());

        clientDao.save(client);

        Hibernate.initialize(client.getOrders());

        return client.getOrders();
    }

    Order verifyOrder(final Client client, final UUID orderId){

        final Order order = clientDao.findOrderForUpdate(client.getId(), orderId)
                .orElseThrow(() -> {
                    log.error("Could not resolve Order [{}] belonging to Client [{}]", orderId, client.getId());
                    return new IllegalStateException();
                });

        return order;

    }

}
