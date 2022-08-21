package com.melita_task.melita;

import lombok.Data;
import lombok.NonNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Customer {
    final int id;
    @NonNull String name;
    @NonNull String surname;
    @NonNull String address;
    List<JSONObject> services = new ArrayList<>();

    public Customer(int id, Map<String, String> userDetails){
        this.id = id;
        this.name = userDetails.get("name");
        this.surname = userDetails.get("surname");
        this.address = userDetails.get("address");
    }

    public Customer(int id, String name, String surname, String address){
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.address = address;
    }

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

    public void attachService(Service service_type, Date date) throws JSONException {
        JSONObject newService = new JSONObject();
        newService.put("Service", service_type);
        newService.put("PreferredInstallationDateTime", date);
        services.add(newService);
    }

    public List<JSONObject> getServices(){
        return services;
    }

    public String getServiceWithId(){ return id +": "+getServices().toString(); }

    public Map<String, Object> getMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("surname", surname);
        map.put("address", address);
        map.put("services", services);

        return map;
    }
}
