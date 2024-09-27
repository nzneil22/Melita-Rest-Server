package com.melita_task.api.amqp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.melita_task.api.models.Client;
import com.melita_task.api.models.FullName;
import com.melita_task.api.models.InstallationAddress;
import com.melita_task.contract.ClientDto;
import com.melita_task.contract.enums.EventTypes;
import com.melita_task.contract.events.CreateClientEventDto;
import com.melita_task.contract.events.SubmitOrdersEventDto;
import com.melita_task.contract.events.UpdateClientEventDto;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ImportAutoConfiguration(classes = MessageProducer.class)
class MessageProducerTest {

    @Autowired
    private MessageCollector messageCollector;

    @Autowired
    private MessageProducer sut;

    @Autowired
    private AMQPBindings cs;

    @Autowired
    private MapperFacade mapper;

    @Test
    void sendEvent_withCreateClientEventObjectAsPayload_shouldSendMessageOnCrmQueueCreate() throws InterruptedException, JsonProcessingException {

        final Client client = new Client(new FullName("name", "middleName", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final ClientDto clientDto = mapper.map(client, ClientDto.class);

        final CreateClientEventDto createClientEventDto = new CreateClientEventDto(clientDto);

        sut.sendEvent(createClientEventDto);

        String json = (String) messageCollector.forChannel(cs.crmQueueCreate()).poll(10, TimeUnit.SECONDS).getPayload();

        final CreateClientEventDto createClientEventDtoReceived = new ObjectMapper().readValue(json, CreateClientEventDto.class);

        log.info("GOT: <<{}>>", json);

        Assertions.assertThat(createClientEventDtoReceived.getType()).isEqualTo(EventTypes.CLIENT_CREATED);
        Assertions.assertThat(createClientEventDtoReceived.getClient()).isEqualTo(clientDto);

    }

    @Test
    void sendEvent_withUpdateClientEventObjectAsPayload_shouldSendMessageOnCrmQueueUpdate() throws InterruptedException, JsonProcessingException {

        final Client client = new Client(new FullName("name", "middleName", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final ClientDto clientDto = mapper.map(client, ClientDto.class);

        final UpdateClientEventDto updateClientEventDto = new UpdateClientEventDto(clientDto);

        sut.sendEvent(updateClientEventDto);

        String json = (String) messageCollector.forChannel(cs.crmQueueUpdate()).poll(10, TimeUnit.SECONDS).getPayload();

        final UpdateClientEventDto updateClientEventDtoReceived = new ObjectMapper().readValue(json, UpdateClientEventDto.class);

        log.info("GOT: <<{}>>", json);

        Assertions.assertThat(updateClientEventDtoReceived.getType()).isEqualTo(EventTypes.CLIENT_UPDATED);
        Assertions.assertThat(updateClientEventDtoReceived.getClient()).isEqualTo(clientDto);

    }

    @Test
    void sendEvent_withSubmitOrdersEventObjectAsPayload_shouldSendMessageOnCrmQueueSubmit() throws InterruptedException, JsonProcessingException {

        final Client client = new Client(new FullName("name", "middleName", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final ClientDto clientDto = mapper.map(client, ClientDto.class);

        final SubmitOrdersEventDto submitOrdersEventDto = new SubmitOrdersEventDto(clientDto);

        sut.sendEvent(submitOrdersEventDto);

        String json = (String) messageCollector.forChannel(cs.crmQueueSubmit()).poll(10, TimeUnit.SECONDS).getPayload();

        final SubmitOrdersEventDto submitOrdersEventDtoReceived = new ObjectMapper().readValue(json, SubmitOrdersEventDto.class);

        log.info("GOT: <<{}>>", json);

        Assertions.assertThat(submitOrdersEventDtoReceived.getType()).isEqualTo(EventTypes.ORDER_SUBMIT);
        Assertions.assertThat(submitOrdersEventDtoReceived.getClient()).isEqualTo(clientDto);

    }

}