package com.melita_task.api.mapper;

import com.melita_task.api.models.Client;
import com.melita_task.api.models.Order;
import com.melita_task.contract.ClientDto;
import com.melita_task.contract.NewClientRequestDto;
import com.melita_task.contract.OrderDto;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper extends ConfigurableMapper {

    @Override
    public void configure(MapperFactory factory) {
        super.configure(factory);
        factory.classMap(Client.class, ClientDto.class)
                .byDefault()
                .register();

        factory.classMap(NewClientRequestDto.class, Client.class)
                .byDefault()
                .register();

        factory.classMap(NewClientRequestDto.class, ClientDto.class)
                .byDefault()
                .register();

        factory.classMap(Order.class, OrderDto.class)
                .byDefault()
                .register();


    }
}
