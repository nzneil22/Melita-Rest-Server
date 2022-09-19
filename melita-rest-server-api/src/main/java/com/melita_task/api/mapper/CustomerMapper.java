package com.melita_task.api.mapper;

import com.melita_task.api.models.Client;
import com.melita_task.api.models.Orders;
import com.melita_task.contract.ClientDto;
import com.melita_task.contract.NewClientRequestDto;
import com.melita_task.contract.OrderDto;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerMapper extends ConfigurableMapper {

    @Override
    public void configure(MapperFactory factory) {
        super.configure(factory);
        factory.classMap(Client.class, ClientDto.class)
                .byDefault()
                .register();

        factory.classMap(NewClientRequestDto.class, Client.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(NewClientRequestDto source, Client destination, MappingContext mappingContext) {
                        destination.setId(UUID.randomUUID().toString());
                    }
                })

                .byDefault()
                .register();

        factory.classMap(NewClientRequestDto.class, ClientDto.class)
                .byDefault()
                .register();

        factory.classMap(Orders.class, OrderDto.class)
                .byDefault()
                .register();


    }
}
