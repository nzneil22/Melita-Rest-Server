package com.melita_task.api.controllers;

import com.melita_task.api.amqp.MessageProducer;
import com.melita_task.api.models.FullNameUpdate;
import com.melita_task.api.models.InstallationAddressUpdate;
import com.melita_task.api.models.requests.CreateClientRequest;
import com.melita_task.api.models.requests.UpdateClientRequest;
import com.melita_task.api.service.ClientService;
import com.melita_task.contract.ClientDto;
import com.melita_task.contract.enums.ClientStatus;
import com.melita_task.contract.events.CreateClientEventDto;
import com.melita_task.contract.events.UpdateClientEventDto;
import com.melita_task.contract.requests.CreateClientRequestDto;
import com.melita_task.contract.requests.UpdateClientRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/clients")
@RequiredArgsConstructor
public class ClientController {

    private final MessageProducer messageProducer;

    private final MapperFacade mapper;
    private final ClientService clientService;

    @PostMapping
    public ClientDto createClient(@RequestBody @NotNull @Valid final CreateClientRequestDto requestDto) {

        final CreateClientRequest request = mapper.map(requestDto, CreateClientRequest.class);

        final ClientDto clientDto = mapper.map(clientService.registerClient(request), ClientDto.class);

        messageProducer.sendEvent(new CreateClientEventDto(clientDto));

        return clientDto;
    }

    @GetMapping(path = "/{clientId}")
    public ResponseEntity<ClientDto> getClient(@PathVariable final UUID clientId) {
        // if service returns empty optional, respond with 404
        return clientService.findClient(clientId, true)
                .map(client -> mapper.map(client, ClientDto.class))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping(path = "/{clientId}")
    public ClientDto editClient(@PathVariable @NotNull final UUID clientId,
                                @RequestBody final UpdateClientRequestDto requestDto) {

        final FullNameUpdate fullNameUpdate;
        if(requestDto.getFullName().isPresent()){
            fullNameUpdate = mapper.map(requestDto.getFullName().get(), FullNameUpdate.class);
        }else{
            fullNameUpdate = null;
        }

        final InstallationAddressUpdate installationAddressUpdate;
        if(requestDto.getInstallationAddress().isPresent()){
            installationAddressUpdate = mapper.map(requestDto.getInstallationAddress().get(), InstallationAddressUpdate.class);
        }else{
            installationAddressUpdate = null;
        }

        final UpdateClientRequest request = new UpdateClientRequest(fullNameUpdate, installationAddressUpdate);

        final ClientDto clientDto = mapper.map(clientService.updateClient(clientId, request), ClientDto.class);

        messageProducer.sendEvent(new UpdateClientEventDto(clientDto));

        return clientDto;
    }

    @PutMapping("/{clientId}/status")
    public ClientDto clientStatus(@PathVariable @NotNull final UUID clientId,
                                  @RequestBody @NotNull @Valid final ClientStatus status) {

        final ClientDto clientDto = mapper.map(clientService.clientStatus(clientId, status), ClientDto.class);

        messageProducer.sendEvent(new UpdateClientEventDto(clientDto));

        return clientDto;
    }
}
