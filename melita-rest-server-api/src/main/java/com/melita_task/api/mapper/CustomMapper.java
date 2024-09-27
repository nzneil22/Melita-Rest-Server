package com.melita_task.api.mapper;

import com.melita_task.api.models.Client;
import com.melita_task.api.models.Order;
import com.melita_task.api.models.requests.CreateClientRequest;
import com.melita_task.api.models.requests.CreateOrderRequest;
import com.melita_task.api.models.requests.UpdateOrderRequest;
import com.melita_task.contract.ClientDto;
import com.melita_task.contract.ClientDtoRabbit;
import com.melita_task.contract.OrderDto;
import com.melita_task.contract.requests.CreateClientRequestDto;
import com.melita_task.contract.requests.CreateOrderRequestDto;
import com.melita_task.contract.requests.UpdateOrderRequestDto;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomMapper extends ConfigurableMapper {

    @Override
    public void configure(MapperFactory factory) {
        super.configure(factory);
        factory.classMap(Client.class, ClientDto.class)
                .byDefault()
                .register();


        factory.classMap(Order.class, OrderDto.class)
                .customize(new ma.glasnost.orika.CustomMapper<>() {
                    @Override
                    public void mapAtoB(Order order, OrderDto orderDto, MappingContext context) {
                        orderDto.setClientId(order.getClient().getId());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Client.class, ClientDtoRabbit.class)
                .byDefault()
                .register();

        factory.classMap(CreateClientRequestDto.class, ClientDto.class)
                .byDefault()
                .register();


        factory.classMap(CreateClientRequest.class, CreateClientRequestDto.class)
                .byDefault()
                .register();

        factory.classMap(CreateOrderRequest.class, CreateOrderRequestDto.class)
                .byDefault()
                .register();

        factory.classMap(UpdateOrderRequest.class, UpdateOrderRequestDto.class)
                .exclude("lobType")
                .exclude("serviceId")
                .exclude("installationDate")
                .customize(new ma.glasnost.orika.CustomMapper<>() {
                    @Override
                    public void mapAtoB(UpdateOrderRequest updateOrderRequest, UpdateOrderRequestDto updateOrderRequestDto, MappingContext context) {

                        updateOrderRequestDto.setLobType(updateOrderRequest.getLobTypeConcrete());
                        updateOrderRequestDto.setServiceId(updateOrderRequest.getServiceIdConcrete());
                        updateOrderRequestDto.setInstallationDate(updateOrderRequest.getInstallationDateConcrete());

                    }
                })
                .byDefault()
                .register();

    }
}
