package com.melita_task.api.controllers;

import com.melita_task.api.models.requests.CreateOrderRequest;
import com.melita_task.api.models.requests.UpdateOrderRequest;
import com.melita_task.api.service.OrderService;
import com.melita_task.contract.OrderDto;
import com.melita_task.contract.requests.CreateOrderRequestDto;
import com.melita_task.contract.requests.UpdateOrderRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/clients/{clientId}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final MapperFacade mapper;
    private final OrderService orderService;

    @PostMapping
    public OrderDto addOrder(@PathVariable @NotNull final UUID clientId,
                             @RequestBody @NotNull @Valid final CreateOrderRequestDto request) {

        CreateOrderRequest oRequest = mapper.map(request, CreateOrderRequest.class);
        return mapper.map(orderService.addOrder(clientId, oRequest), OrderDto.class);
    }

    @GetMapping
    public List<OrderDto> getOrders(@PathVariable @NotNull final UUID clientId) {
        return orderService
                .getClientOrders(clientId)
                .stream()
                .map(o -> mapper.map(o, OrderDto.class))
                .collect(Collectors.toList());
    }

    @PutMapping("/submit")
    public List<OrderDto> submitClientOrders(@PathVariable @NotNull final UUID clientId){
        return orderService
                .submitOrders(clientId)
                .stream()
                .map(o -> mapper.map(o, OrderDto.class))
                .collect(Collectors.toList());
    }

    @PutMapping("/{orderId}")
    public OrderDto editOrder(@PathVariable @NotNull final UUID clientId,
                              @PathVariable @NotNull final UUID orderId,
                              @RequestBody @NotNull final UpdateOrderRequestDto oUpdateDto) {
        final UpdateOrderRequest oUpdate = mapper.map(oUpdateDto, UpdateOrderRequest.class);
        return mapper.map(orderService.editOrder(clientId, orderId, oUpdate), OrderDto.class);
    }

    @DeleteMapping("/{orderId}")
    public String deleteOrder(@PathVariable @NotNull final UUID clientId,
                              @PathVariable @NotNull final UUID orderId) {
        return orderService.cancelOrder(clientId, orderId);
    }

}
