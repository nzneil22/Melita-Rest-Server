package com.melita_task.api.service;

import com.melita_task.api.exceptions.LogicalErrorException;
import com.melita_task.api.exceptions.ServiceIdExistsException;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductCatalogService {
    private List<Integer> prodCatalog = new ArrayList<>();

    public boolean isServiceIdValid(Integer id){
        if (id == null) throw new LogicalErrorException("SERVICE_ID_CANNOT_BE_NULL");
        return getProductCatalog().contains(id);
    }

    @Cacheable(value = "prodCatalog", key = "'catalog'")
    public List<Integer> getProductCatalog(){
        return prodCatalog;
    }

    @CachePut(value = "prodCatalog", key = "'catalog'")
    public List<Integer> addProdCatalog(int servId){
        this.prodCatalog = getProductCatalog();
        if(prodCatalog.contains(servId))
            throw new ServiceIdExistsException();
        prodCatalog.add(servId);
        return prodCatalog;
    }

    @CachePut(value = "prodCatalog", key = "'catalog'")
    public List<Integer> remProdCatalog(int servId){
        this.prodCatalog = getProductCatalog();
        if(!prodCatalog.contains(servId))
            throw new EntityNotFoundException();
        prodCatalog.remove((Integer) servId);
        return prodCatalog;
    }
}
