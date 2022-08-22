package com.melita_task.melita;

import com.melita_task.amqp.ClientSource;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

@Service
@EnableBinding(ClientSource.class)
public class MessageConsumer{

    @StreamListener(target = ClientSource.LISTEN)
    public void onMessage(String msg){
        System.out.println("Melita Consumes Message - " + msg);
    }

}