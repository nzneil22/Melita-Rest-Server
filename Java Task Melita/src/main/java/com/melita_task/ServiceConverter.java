package com.melita_task;

import com.melita_task.melita.Service;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class ServiceConverter implements Converter<String, Service> {
    @Override
    public Service convert(String value) {
        try {
            return Service.valueOf(value);
        } catch(IllegalArgumentException iae){
            return Service.valueOf("ERROR");
        }
    }
}
