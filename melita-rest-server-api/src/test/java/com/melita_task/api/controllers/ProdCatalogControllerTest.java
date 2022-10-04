package com.melita_task.api.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.melita_task.api.WebSecurityConfig;
import com.melita_task.api.service.ProductCatalogService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@WithMockUser(authorities = "ROLE_ADMIN")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers =  ProdCatalogController.class, includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {ProductCatalogService.class, WebSecurityConfig.class}))
class ProdCatalogControllerTest{

    @MockBean
    private ProductCatalogService productCatalogService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getProdCatalog_givenAnEmptyProductCatalog_shouldReturnTheProductLog() throws Exception {

        final List<Integer> prodCatalog = new ArrayList<>();

        Mockito.when(productCatalogService.getProdCatalog()).thenReturn(prodCatalog);

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/prodCatalog"))
                    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        List<Integer> response = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});

        Assertions.assertThat(response).isEqualTo(prodCatalog).isEmpty();

        Mockito.verify(productCatalogService, Mockito.times(1)).getProdCatalog();

    }

    @Test
    public void getProdCatalog_givenThatProductCatalogIsNotAnEmptyList_shouldReturnTheProductCatalog() throws Exception {

        final List<Integer> prodCatalog = List.of(100);

        Mockito.when(productCatalogService.getProdCatalog()).thenReturn(prodCatalog);

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/prodCatalog"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        List<Integer> response = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});

        Assertions.assertThat(response).isEqualTo(prodCatalog);

        Mockito.verify(productCatalogService, Mockito.times(1)).getProdCatalog();
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void addProdCatalog_givenThatProductCatalogIsAnEmptyListAndAValidServiceIdIsGiven_shouldReturnTheProductCatalogNowContainingNewValue() throws Exception {

        final Integer newServiceId = 100;
        final List<Integer> prodCatalog = List.of(newServiceId);

        Mockito.when(productCatalogService.addProdCatalog(newServiceId)).thenReturn(prodCatalog);

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/prodCatalog/{id}", newServiceId))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        List<Integer> response = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});

        Assertions.assertThat(response).contains(newServiceId);

        Mockito.verify(productCatalogService, Mockito.times(1)).addProdCatalog(newServiceId);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void addProdCatalog_givenThatProductCatalogIsAnEmptyListButTheServiceIdIsAlreadyInProductCatalog_shouldReturnTheProductCatalogContainingTheValue() throws Exception {

        final Integer newServiceId = 100;

        final List<Integer> prodCatalog = List.of(newServiceId);


        Mockito.when(productCatalogService.addProdCatalog(newServiceId)).thenReturn(prodCatalog);

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/prodCatalog/{id}", newServiceId))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        List<Integer> response = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});

        Assertions.assertThat(response).contains(newServiceId);

        Mockito.verify(productCatalogService, Mockito.times(1)).addProdCatalog(newServiceId);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void remProdCatalog_givenThatProductCatalogIsNotEmptyListAndTheServiceIdIsAlreadyInProductCatalog_shouldReturnTheProductCatalogNotContainingTheValue() throws Exception {

        final Integer newServiceId = 100;

        List<Integer> prodCatalog = List.of(200);

        Mockito.when(productCatalogService.remProdCatalog(newServiceId)).thenReturn(prodCatalog);

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/prodCatalog/{id}", newServiceId))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        List<Integer> response = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});

        Assertions.assertThat(response).doesNotContain(newServiceId);

        Mockito.verify(productCatalogService, Mockito.times(1)).remProdCatalog(newServiceId);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void remProdCatalog_givenThatProductCatalogDoesNotTheServiceIdToBeRemoved_shouldThrowEntityNotFoundException() throws Exception {

        final int newServiceId = 100;

        Mockito.when(productCatalogService.remProdCatalog(newServiceId)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/prodCatalog/{id}", newServiceId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());


        Mockito.verify(productCatalogService, Mockito.times(1)).remProdCatalog(newServiceId);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void isInProductCatalog_givenThatProductCatalogDoesNotContainTheServiceId_shouldThrowEntityNotFoundException() throws Exception {

        final int newServiceId = 100;

        Mockito.when(productCatalogService.isServiceIdValid(newServiceId)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/prodCatalog/{id}", newServiceId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());


        Mockito.verify(productCatalogService, Mockito.times(1)).isServiceIdValid(newServiceId);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void isInProductCatalog_givenThatProductCatalogContainsTheServiceId_shouldReturnTrue() throws Exception {

        final int newServiceId = 100;

        final List<Integer> prodCatalog = Arrays.asList(100, 200, 300);

        Mockito.when(productCatalogService.isServiceIdValid(newServiceId)).thenReturn(prodCatalog.contains(newServiceId));

        final MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/prodCatalog/{id}", newServiceId))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        final boolean response = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), Boolean.class);

        Assertions.assertThat(response).isTrue();

        Mockito.verify(productCatalogService, Mockito.times(1)).isServiceIdValid(newServiceId);
    }

}