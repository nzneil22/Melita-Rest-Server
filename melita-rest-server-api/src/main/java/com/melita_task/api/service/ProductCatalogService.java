package com.melita_task.api.service;

import com.melita_task.api.exceptions.LogicalErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCatalogService {

    private final ProdCatalogBean prodCatalogBean;

    @PreAuthorize("hasRole('ADMIN')")
    public boolean isServiceIdValid(Integer id){
        if (id == null) throw new LogicalErrorException("SERVICE_ID_CANNOT_BE_NULL");
        if (!prodCatalogBean.getProductCatalog().contains(id)) throw new EntityNotFoundException("SERVICE_ID_NOT_FOUND");
        return true;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Integer> getProdCatalog(){
        return prodCatalogBean.getProductCatalog();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Integer> addProdCatalog(int servId){
        return prodCatalogBean.addProdCatalog(servId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Integer> remProdCatalog(int servId){
        return prodCatalogBean.remProdCatalog(servId);
    }


}
