package com.melita_task.melita;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class Customer {
    final int id;
    @NonNull String name;
    @NonNull String surname;
    @NonNull String address;
    List<Service> services = new ArrayList<>();

    private void setName(String name){
        this.name = name;
    }

    private void setSurname(String surname){
        this.surname = surname;
    }

    private void setAddress(String address){
        this.address = address;
    }

    public void updateCustomer(Map<String, String> details){
        if(details.containsKey("name"))setName(details.get("name"));
        if(details.containsKey("surname"))setSurname(details.get("surname"));
        if(details.containsKey("address"))setAddress(details.get("address"));
    }

    public void attachService(Service service_type){
        services.add(service_type);
    }

    public List<Service> getServices(){
        return services;
    }
}
