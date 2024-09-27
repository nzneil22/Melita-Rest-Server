package com.melita_task.api.service;

import com.melita_task.api.exceptions.LogicalErrorException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@WithMockUser(authorities = "ROLE_ADMIN")
@ExtendWith(SpringExtension.class)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@SpringBootTest(classes = ProdCatalogServiceTest.ProdCatalogServiceConfig.class)
class ProdCatalogServiceTest {

    @Autowired
    private ProductCatalogService sut;

    @Test
    void getProdCatalog_productCatalogEmpty_ShouldReturnAnEmptyList(){
        Assertions.assertThat(sut.getProdCatalog())
                .isEmpty();
    }

    @Test
    void getProdCatalog_productCatalogNotEmpty_ShouldReturnAListOfValidServiceIds(){

        final int serviceId = 100;

        sut.addProdCatalog(serviceId);

        Assertions.assertThat(sut.getProdCatalog())
                .isNotEmpty();

        sut.remProdCatalog(serviceId);
    }

    @Test
    void addProdCatalog_serviceIdNotInProductCatalog_ShouldReturnAListContainingTheNewServiceId(){
        final int serviceId = 100;

        Assertions.assertThat(sut.addProdCatalog(serviceId))
                .contains(serviceId);

        sut.remProdCatalog(serviceId);
    }

    @Test
    void addProdCatalog_serviceIdAlreadyInProductCatalog_ShouldReturnAListContainingTheServiceIdWithoutDuplicates(){
        final int serviceId = 100;

        final List<Integer> prodCat = sut.addProdCatalog(serviceId);

        Assertions.assertThat(sut.addProdCatalog(serviceId))
                .isEqualTo(prodCat);

        sut.remProdCatalog(serviceId);
    }

    @Test
    void remProdCatalog_serviceIdInProductCatalog_ShouldReturnAListNotContainingTheServiceId(){
        final int serviceId = 100;
        sut.addProdCatalog(serviceId);

        Assertions.assertThat(sut.remProdCatalog(serviceId))
                .doesNotContain(serviceId);
    }

    @Test
    void remProdCatalog_serviceIdNotInProductCatalog_ShouldThrowEntityNotFoundException(){
        final int serviceId = -1;

        Assertions.assertThatThrownBy(() -> sut.remProdCatalog(serviceId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void isServiceIdValid_serviceIdIsNull_ShouldThrowLogicalErrorException(){
        Assertions.assertThatThrownBy(() -> sut.isServiceIdValid(null))
                .isInstanceOf(LogicalErrorException.class);
    }

    @Test
    void isServiceIdValid_serviceIdIsNotInProdCatalog_ShouldThrowEntityNotFoundException(){
        final int serviceId = -1;

        Assertions.assertThatThrownBy(() -> sut.isServiceIdValid(serviceId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void isServiceIdValid_serviceIdIsInProdCatalog_ShouldReturnTrue(){
        final int serviceId = 100;

        sut.addProdCatalog(serviceId);

        Assertions.assertThat(sut.isServiceIdValid(serviceId))
                .isTrue();

        sut.remProdCatalog(serviceId);
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void getProdCatalog_withNonAdminUser_ShouldThrowAccessDeniedException(){

        Assertions.assertThatThrownBy(() -> sut.getProdCatalog())
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void addProdCatalog_withNonAdminUser_ShouldThrowAccessDeniedException(){
        final int serviceId = 100;

        Assertions.assertThatThrownBy(() -> sut.addProdCatalog(serviceId))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void remProdCatalog_withNonAdminUser_ShouldThrowAccessDeniedException(){
        final int serviceId = 100;

        Assertions.assertThatThrownBy(() -> sut.remProdCatalog(serviceId))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void isServiceIdValid_withNonAdminUser_ShouldThrowAccessDeniedException(){
        final int serviceId = 100;

        Assertions.assertThatThrownBy(() -> sut.isServiceIdValid(serviceId))
                .isInstanceOf(AccessDeniedException.class);
    }

    public static class ProdCatalogServiceConfig {

        @Bean
        public ProductCatalogService productCatalogService(final ProdCatalogBean prodCatalogBean) {

            return new ProductCatalogService(prodCatalogBean);
        }

        @Bean
        public ProdCatalogBean prodCatalogBean() {
            return new ProdCatalogBean();
        }

    }
}



