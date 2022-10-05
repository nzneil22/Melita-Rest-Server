package com.melita_task.api.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.melita_task.api.WebSecurityConfig;
import com.melita_task.api.amqp.MessageProducer;
import com.melita_task.api.exceptions.InvalidServiceIdException;
import com.melita_task.api.exceptions.OrderSubmittedException;
import com.melita_task.api.mapper.CustomMapper;
import com.melita_task.api.models.Client;
import com.melita_task.api.models.FullName;
import com.melita_task.api.models.InstallationAddress;
import com.melita_task.api.models.Order;
import com.melita_task.api.models.requests.CreateOrderRequest;
import com.melita_task.api.models.requests.UpdateOrderRequest;
import com.melita_task.api.service.OrderService;
import com.melita_task.contract.OrderDto;
import com.melita_task.contract.enums.LobTypes;
import com.melita_task.contract.enums.OrderStatus;
import com.melita_task.contract.requests.CreateOrderRequestDto;
import com.melita_task.contract.requests.UpdateOrderRequestDto;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.persistence.EntityNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@WithMockUser(authorities = "ROLE_USER")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers =  OrderController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {CustomMapper.class, OrderController.class, WebSecurityConfig.class}))
class OrderControllerTest {

    @Autowired
    private MapperFacade mapper;

    @MockBean
    private MessageProducer messageProducer;

    @MockBean
    private OrderService orderService;

    @Autowired
    private MockMvc mockMvc;

    final String sDate = "31-Dec-2050 23:37:50";
    final Date date = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(sDate);

    final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    OrderControllerTest() throws ParseException {}

    @Test
    public void addOrder_givenInValidCustomerUUID_shouldThrowEntityNotFoundException() throws Exception {

        final CreateOrderRequestDto createOrderRequestDto = new CreateOrderRequestDto(100, LobTypes.INT, date);

        final String json = ow.writeValueAsString(createOrderRequestDto);

        Mockito.when(orderService.addOrder(any(), any())).thenThrow(new EntityNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.post("/clients/{clientId}/orders", UUID.randomUUID()).contentType(APPLICATION_JSON)
                    .content(json))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void addOrder_givenValidUUIDAndValidRequest_shouldReturnOrderDto() throws Exception {

        final Client client = new Client(new FullName("firstnameNEW", "middleName", "surname"),
                new InstallationAddress("islandNEW", "town", "street", "building"));

        final CreateOrderRequest createOrderRequest = new CreateOrderRequest(100, LobTypes.INT, date);

        final Order order = new Order(client, createOrderRequest);

        final CreateOrderRequestDto createOrderRequestDto = new CreateOrderRequestDto(100, LobTypes.INT, date);

        final String json = ow.writeValueAsString(createOrderRequestDto);

        Mockito.when(orderService.addOrder(any(), any())).thenReturn(order);

        final UUID clientId = client.getId();

        final MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/clients/{clientId}/orders", clientId).contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        final OrderDto ord = new ObjectMapper().readValue(response.getResponse().getContentAsString(), OrderDto.class);

        log.info(" -- Response:\n"+ord);

        Assertions.assertThat(ord.getLobType()).isEqualTo(createOrderRequestDto.getLobType());
        Assertions.assertThat(ord.getServiceId()).isEqualTo(createOrderRequestDto.getServiceId());
        Assertions.assertThat(ord.getInstallationDateAndTime()).isEqualTo(createOrderRequestDto.getInstallationDateAndTime());

        Mockito.verify(orderService, Mockito.times(1)).addOrder(clientId, createOrderRequest);

    }

    @Test
    public void getOrders_givenValidClientUUID_shouldReturnListOfOrderDto() throws Exception {

        final Client client = new Client(new FullName("firstnameNEW", "middleName", "surname"),
                new InstallationAddress("islandNEW", "town", "street", "building"));

        final UUID clientId = client.getId();

        final CreateOrderRequest createOrderRequest = new CreateOrderRequest(100, LobTypes.INT, date);

        client.addOrder(createOrderRequest);
        client.addOrder(createOrderRequest);

        Mockito.when(orderService.getClientOrders(any())).thenReturn(client.getOrders());

        final MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/clients/{clientId}/orders", clientId).contentType(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        List<OrderDto> ords = new ObjectMapper().readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});

        ords.forEach(ord -> log.info(" -- Response:\n"+ord));

        Assertions.assertThat(ords).hasSize(client.getOrders().size());

        Mockito.verify(orderService, Mockito.times(1)).getClientOrders(clientId);

    }

    @Test
    public void getOrders_givenInValidClientUUID_shouldThrowEntityNotFoundException() throws Exception {

        Mockito.when(orderService.getClientOrders(any())).thenThrow(EntityNotFoundException.class);

        final UUID clientId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.get("/clients/{clientId}/orders", clientId).contentType(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(orderService, Mockito.times(1)).getClientOrders(clientId);

    }

    @Test
    public void submitOrders_givenInValidClientUUID_shouldThrowEntityNotFoundException() throws Exception {

        Mockito.when(orderService.submitOrders(any())).thenThrow(EntityNotFoundException.class);

        final UUID clientId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.put("/clients/{clientId}/orders/submit", clientId).contentType(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(orderService, Mockito.times(1)).submitOrders(clientId);
    }

    @Test
    public void submitOrders_givenValidClientUUID_shouldReturnListOfOrderDto() throws Exception {

        final Client client = new Client(new FullName("firstnameNEW", "middleName", "surname"),
                new InstallationAddress("islandNEW", "town", "street", "building"));

        final UUID clientId = client.getId();

        final CreateOrderRequest createOrderRequest = new CreateOrderRequest(100, LobTypes.INT, date);

        client.addOrder(createOrderRequest);
        client.addOrder(createOrderRequest);

        client.getOrders().forEach(order -> order.setStatus(OrderStatus.SUBMITTED));

        Mockito.when(orderService.submitOrders(any())).thenReturn(client);

        final MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/clients/{clientId}/orders/submit", clientId).contentType(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        List<OrderDto> ords = new ObjectMapper().readValue(response.getResponse().getContentAsString(), new TypeReference<>() {});

        ords.forEach(ord -> log.info(" -- Response:\n"+ord));

        Assertions.assertThat(ords)
                .filteredOn(ord -> OrderStatus.SUBMITTED.equals(ord.getStatus()))
                .hasSize(client.getOrders().size());

        Mockito.verify(orderService, Mockito.times(1)).submitOrders(clientId);
    }

    @Test
    void editOrder_givenCorrectClientUUIDAndValidOrderUUID_shouldReturnTheEditedOrder() throws Exception {
        final Client client = new Client(new FullName("firstnameNEW", "middleName", "surname"),
                new InstallationAddress("islandNEW", "town", "street", "building"));

        final UUID clientId = client.getId();

        final CreateOrderRequest createOrderRequest = new CreateOrderRequest(200, LobTypes.INT, date);
        client.addOrder(createOrderRequest);
        final Order order = client.getOrders().get(0);
        final UUID orderId = order.getId();

        final UpdateOrderRequestDto updateOrderRequestDto =  new UpdateOrderRequestDto(100, LobTypes.MOB, date);
        order.updateOrder(mapper.map(updateOrderRequestDto, UpdateOrderRequest.class));

        final String updateOrderRequestDtoJson = ow.writeValueAsString(updateOrderRequestDto);

        Mockito.when(orderService.editOrder(any(), any(), any())).thenReturn(order);

        final MvcResult response = mockMvc.perform(MockMvcRequestBuilders
                        .put("/clients/{clientId}/orders/{orderId}", clientId, orderId)
                        .contentType(APPLICATION_JSON).content(updateOrderRequestDtoJson))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        OrderDto ord = new ObjectMapper().readValue(response.getResponse().getContentAsString(), OrderDto.class);

        log.info(" -- Response:\n"+ord);

        Assertions.assertThat(ord.getServiceId()).isEqualTo(updateOrderRequestDto.getServiceId());
        Assertions.assertThat(ord.getLobType()).isEqualTo(updateOrderRequestDto.getLobType());
        Assertions.assertThat(ord.getInstallationDateAndTime()).isEqualTo(updateOrderRequestDto.getInstallationDate());

        final UpdateOrderRequest updateOrderRequest = mapper.map(updateOrderRequestDto, UpdateOrderRequest.class);
        Mockito.verify(orderService, Mockito.times(1)).editOrder(clientId, orderId, updateOrderRequest);
    }

    @Test
    void editOrder_givenNonMatchingClientAndOrderUUIDs_shouldThrowIllegalStateException() throws Exception {

        final UUID clientId = UUID.randomUUID();
        final UUID orderId = UUID.randomUUID();

        final UpdateOrderRequestDto updateOrderRequestDto =  new UpdateOrderRequestDto(100, LobTypes.MOB, date);
        final String updateOrderRequestDtoJson = ow.writeValueAsString(updateOrderRequestDto);

        Mockito.when(orderService.editOrder(any(), any(), any())).thenThrow(IllegalStateException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/clients/{clientId}/orders/{orderId}", clientId, orderId)
                        .contentType(APPLICATION_JSON).content(updateOrderRequestDtoJson))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());

        final UpdateOrderRequest updateOrderRequest = mapper.map(updateOrderRequestDto, UpdateOrderRequest.class);
        Mockito.verify(orderService, Mockito.times(1)).editOrder(clientId, orderId, updateOrderRequest);
    }

    @Test
    void editOrder_givenCorrectClientUUIDAndValidOrderUUIDButOrderIsAlreadySubmitted_shouldThrowOrderSubmittedException() throws Exception {
        final UUID clientId = UUID.randomUUID();
        final UUID orderId = UUID.randomUUID();

        final UpdateOrderRequestDto updateOrderRequestDto =  new UpdateOrderRequestDto(100, LobTypes.MOB, date);
        final String updateOrderRequestDtoJson = ow.writeValueAsString(updateOrderRequestDto);

        Mockito.when(orderService.editOrder(any(), any(), any())).thenThrow(OrderSubmittedException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/clients/{clientId}/orders/{orderId}", clientId, orderId)
                        .contentType(APPLICATION_JSON).content(updateOrderRequestDtoJson))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

        final UpdateOrderRequest updateOrderRequest = mapper.map(updateOrderRequestDto, UpdateOrderRequest.class);
        Mockito.verify(orderService, Mockito.times(1)).editOrder(clientId, orderId, updateOrderRequest);
    }

    @Test
    void editOrder_givenCorrectClientUUIDAndValidOrderUUIDOrderIsAlreadyCreatedButUpdateObjectHasInvalidServiceId_shouldThrowInvalidServiceIdException() throws Exception {
        final UUID clientId = UUID.randomUUID();
        final UUID orderId = UUID.randomUUID();

        final UpdateOrderRequestDto updateOrderRequestDto =  new UpdateOrderRequestDto(100, LobTypes.MOB, date);
        final String updateOrderRequestDtoJson = ow.writeValueAsString(updateOrderRequestDto);

        Mockito.when(orderService.editOrder(any(), any(), any())).thenThrow(InvalidServiceIdException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/clients/{clientId}/orders/{orderId}", clientId, orderId)
                        .contentType(APPLICATION_JSON).content(updateOrderRequestDtoJson))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

        final UpdateOrderRequest updateOrderRequest = mapper.map(updateOrderRequestDto, UpdateOrderRequest.class);
        Mockito.verify(orderService, Mockito.times(1)).editOrder(clientId, orderId, updateOrderRequest);
    }

    @Test
    void deleteOrder_givenCorrectClientUUIDAndValidOrderUUIDButOrderIsAlreadySubmitted_shouldThrowOrderSubmittedException() throws Exception {
        final UUID clientId = UUID.randomUUID();
        final UUID orderId = UUID.randomUUID();

        Mockito.when(orderService.cancelOrder(any(), any())).thenThrow(OrderSubmittedException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/clients/{clientId}/orders/{orderId}", clientId, orderId)
                        .contentType(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

        Mockito.verify(orderService, Mockito.times(1)).cancelOrder(clientId, orderId);
    }

    @Test
    void deleteOrder_givenCorrectClientUUIDAndValidOrderUUIDAndOrderIsAlreadyCreatedButOrderDoesNotBelongToClient_shouldThrowIllegalStateException() throws Exception {
        final UUID clientId = UUID.randomUUID();
        final UUID orderId = UUID.randomUUID();

        Mockito.when(orderService.cancelOrder(any(), any())).thenThrow(IllegalStateException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/clients/{clientId}/orders/{orderId}", clientId, orderId)
                        .contentType(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());

        Mockito.verify(orderService, Mockito.times(1)).cancelOrder(clientId, orderId);
    }

    @Test
    void deleteOrder_givenCorrectClientUUIDAndValidOrderUUIDAndOrderIsCreatedAndOrderDoesBelongToClient_shouldReturnAStringSayingThatTheOrderWasDeleted() throws Exception {
        final UUID clientId = UUID.randomUUID();
        final UUID orderId = UUID.randomUUID();

        Mockito.when(orderService.cancelOrder(any(), any())).thenReturn("Deleted Order with id: " + orderId);

        final MvcResult response = mockMvc.perform(MockMvcRequestBuilders
                        .delete("/clients/{clientId}/orders/{orderId}", clientId, orderId)
                        .contentType(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        final String responseS = response.getResponse().getContentAsString();

        Assertions.assertThat(responseS).isEqualTo("Deleted Order with id: " + orderId);

        Mockito.verify(orderService, Mockito.times(1)).cancelOrder(clientId, orderId);
    }

}