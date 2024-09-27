package com.melita_task.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.melita_task.api.WebSecurityConfig;
import com.melita_task.api.amqp.MessageProducer;
import com.melita_task.api.mapper.CustomMapper;
import com.melita_task.api.models.Client;
import com.melita_task.api.models.FullName;
import com.melita_task.api.models.InstallationAddress;
import com.melita_task.api.models.requests.CreateClientRequest;
import com.melita_task.api.service.ClientService;
import com.melita_task.contract.*;
import com.melita_task.contract.requests.UpdateClientRequestDto;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@WithMockUser(authorities = "ROLE_USER")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers =  ClientController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {CustomMapper.class, ClientService.class, WebSecurityConfig.class}))
class ClientControllerTest {

    @MockBean
    private ClientService clientService;

    @MockBean
    private MessageProducer messageProducer;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void   createClient_givenValidCreateClientRequest_shouldReturnClientDTO() throws Exception {

        final Client client = new Client(new FullName("name", "middleName", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        final CreateClientRequest createClientRequest = new CreateClientRequest(new FullNameDto("firstname", "middleName", "lastname"),
                new InstallationAddressDto("island", "town", "street", "building"));

        final String json = ow.writeValueAsString(createClientRequest);

        Mockito.when(clientService.registerClient(any())).thenReturn(client);

        final MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/clients").contentType(APPLICATION_JSON)
                    .content(json))
                    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        final ClientDto clientDto = new ObjectMapper().readValue(response.getResponse().getContentAsString(), ClientDto.class);

        Assertions.assertThat(clientDto.getFullName().getMiddleName()).isEqualTo("middleName");
        Assertions.assertThat(clientDto.getInstallationAddress().getIsland()).isEqualTo("island");

    }

    @Test
    public void createClient_givenInValidCreateClientRequest_shouldReturnServerError() throws Exception {

        final Client client = new Client(new FullName("name", "middleName", "surname"),
                new InstallationAddress("island", "town", "street", "building"));

        final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        final CreateClientRequest createClientRequest = new CreateClientRequest(new FullNameDto(null, null, null),
                new InstallationAddressDto("island", "town", "street", "building"));

        final String json = ow.writeValueAsString(createClientRequest);

        Mockito.when(clientService.registerClient(any())).thenReturn(client);

        mockMvc.perform(MockMvcRequestBuilders.post("/clients").contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }

    @Test
    public void getClient_givenValidUUID_shouldReturnClientDTO() throws Exception {

        final Client client = new Client(new FullName("firstName", "middleName", "lastName"),
                new InstallationAddress("island", "town", "street", "building"));

        Mockito.when(clientService.findClient(any(), anyBoolean())).thenReturn(Optional.of(client));

        final MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/clients/{clientId}", UUID.randomUUID()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        ClientDto clientDto = new ObjectMapper().readValue(response.getResponse().getContentAsString(), ClientDto.class);

        Assertions.assertThat(clientDto.getFullName().getMiddleName()).isEqualTo("middleName");
        Assertions.assertThat(clientDto.getInstallationAddress().getIsland()).isEqualTo("island");
    }

    @Test
    public void getClient_givenInValidUUID_shouldRespondWithNotFound() throws Exception {

        Mockito.when(clientService.findClient(any(), anyBoolean())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/clients/{clientId}", UUID.randomUUID()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void editClient_givenValidUUID_shouldRespondWithEditedClient() throws Exception {

        final Client newClient = new Client(new FullName("firstnameNEW", "middleName", "surname"),
                new InstallationAddress("islandNEW", "town", "street", "building"));

        final UpdateClientRequestDto updateClientRequest = new UpdateClientRequestDto(new FullNameUpdateDto("firstnameNEW", "middleName", "lastname"),
                new InstallationAddressUpdateDto("islandNEW", "town", "street", "building"));

        final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        final String json = ow.writeValueAsString(updateClientRequest);

        Mockito.when(clientService.updateClient(any(UUID.class), any())).thenReturn(newClient);

        final MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/clients/{clientId}", UUID.randomUUID()).contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        final ClientDto clientDto = new ObjectMapper().readValue(response.getResponse().getContentAsString(), ClientDto.class);

        Assertions.assertThat(clientDto.getFullName().getFirstName()).isEqualTo("firstnameNEW");
        Assertions.assertThat(clientDto.getInstallationAddress().getIsland()).isEqualTo("islandNEW");

    }

    @Test
    public void editClient_givenInValidUUID_shouldThrowEntityNotFoundException() throws Exception {

        final UpdateClientRequestDto updateClientRequest = new UpdateClientRequestDto(new FullNameUpdateDto("firstnameNEW", "middleName", "lastname"),
                new InstallationAddressUpdateDto("islandNew", "town", "street", "building"));

        final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        final String json = ow.writeValueAsString(updateClientRequest);

        Mockito.when(clientService.updateClient(any(UUID.class), any())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/clients/{clientId}", UUID.randomUUID()).contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void editClient_givenValidFullNameOnlyUpdateClientRequest_shouldReturnEditedClient() throws Exception {

        final Client newClient = new Client(new FullName("firstnameNEW", "middleName", "surname"),
                new InstallationAddress("islandNEW", "town", "street", "building"));

        final UpdateClientRequestDto updateClientRequest = new UpdateClientRequestDto(new FullNameUpdateDto("firstnameNEW", "middleName", "lastname"),
                null);

        final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        final String json = ow.writeValueAsString(updateClientRequest);

        Mockito.when(clientService.updateClient(any(UUID.class), any())).thenReturn(newClient);

        final MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/clients/{clientId}", UUID.randomUUID()).contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        final ClientDto clientDto = new ObjectMapper().readValue(response.getResponse().getContentAsString(), ClientDto.class);

        Assertions.assertThat(clientDto.getFullName().getFirstName()).isEqualTo("firstnameNEW");
        Assertions.assertThat(clientDto.getInstallationAddress().getIsland()).isEqualTo("islandNEW");
    }

    @Test
    public void editClient_givenValidInstallationAddressOnlyUpdateClientRequest_shouldReturnEditedClient() throws Exception {

        final Client newClient = new Client(new FullName("firstnameNEW", "middleName", "surname"),
                new InstallationAddress("islandNEW", "town", "street", "building"));

        final UpdateClientRequestDto updateClientRequest = new UpdateClientRequestDto(null,
                new InstallationAddressUpdateDto("islandNEW", "town", "street", "building"));

        final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        final String json = ow.writeValueAsString(updateClientRequest);

        Mockito.when(clientService.updateClient(any(UUID.class), any())).thenReturn(newClient);

        final MvcResult response = mockMvc.perform(MockMvcRequestBuilders.put("/clients/{clientId}", UUID.randomUUID()).contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        final ClientDto clientDto = new ObjectMapper().readValue(response.getResponse().getContentAsString(), ClientDto.class);

        Assertions.assertThat(clientDto.getFullName().getFirstName()).isEqualTo("firstnameNEW");
        Assertions.assertThat(clientDto.getInstallationAddress().getIsland()).isEqualTo("islandNEW");
    }

}