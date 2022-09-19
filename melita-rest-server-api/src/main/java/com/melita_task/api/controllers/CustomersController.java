package com.melita_task.api.controllers;

import com.melita_task.api.models.FullName;
import com.melita_task.api.models.InstallationAddress;
import com.melita_task.api.service.ClientsService;
import com.melita_task.contract.ClientDto;
import com.melita_task.contract.NewClientRequestDto;
import com.melita_task.contract.OrderDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/clients")
@RequiredArgsConstructor
public class CustomersController {

    private final MapperFacade mapper;
    private final ClientsService clientService;


    @PostMapping
    public ClientDto createCustomer(@RequestBody @NotNull @Valid final NewClientRequestDto newClientRequest) {
        log.trace("Received request to register new client: {}", newClientRequest);
        return clientService.registerClient(newClientRequest);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ClientDto> getCustomer(@PathVariable final String id) {
        log.trace("Received request to edit client with id: {}", id);
        // if service returns empty optional, respond with 404
        return clientService.findClient(id)
                .map(client -> mapper.map(client, ClientDto.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/{id}/full-name")
    public ClientDto editCustomerFullName(@PathVariable @NonNull final String id, @RequestBody @Valid final FullName fullName) {
        log.trace("Received request to edit client with id: {} using full-name: {}", id, fullName);
        return clientService.updateClient(id, fullName);
    }

    @PutMapping(path = "/{id}/installation-address")
    public ClientDto editCustomerFullName(@PathVariable @NonNull final String id, @RequestBody @Valid final InstallationAddress installationAddress) {
        log.trace("Received request to edit client with id: {} using installation-address: {}", id, installationAddress);
        return clientService.updateClient(id, installationAddress);
    }

    @PostMapping("/{id}/orders")
    public OrderDto addOrder(@PathVariable @NonNull final String id, @RequestBody @NonNull @Valid final OrderDto order) {
        log.trace("Received request to add order to client with id: {}", id);
        return clientService.addOrder(id, order);
    }

    @GetMapping("/{id}/orders")
    public List<OrderDto> getOrders(@PathVariable @NonNull final String id) {
        log.trace("Received request to get orders of client with id: {}", id);
        return clientService.getClientOrders(id);
    }

    @PutMapping("/{id}/orders/submit")
    public List<OrderDto> submitClientOrders(@PathVariable @NonNull final String id){
        log.trace("Received request to submit all orders of client with id: {}", id);
        return clientService.submitOrders(id);
    }


}
