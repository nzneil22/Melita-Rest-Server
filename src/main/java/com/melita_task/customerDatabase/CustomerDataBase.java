package com.melita_task.customerDatabase;

import com.melita_task.exceptions.CustomerNotFoundException;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class CustomerDataBase {
    List<Customer> customers = new ArrayList<>();

    public void addClient(Customer c) {
        customers.add(c);
    }

    public int generateId(){
        if(customers.isEmpty()) return 0;
        return customers.get(customers.size()-1).getId()+1;
    }

    public void deleteCustomer(int id) throws CustomerNotFoundException {
        getCustomer(id);
        customers = customers.stream()
                .filter(c -> c.getId() != id)
                .collect(Collectors.toList());
    }

    public Customer getCustomer(int id) throws CustomerNotFoundException {
        return customers.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id:" + id + " not found"));
    }

    public void editCustomer(int id, Map<String, String> details) throws CustomerNotFoundException {
        Customer c = getCustomer(id);
        c.updateCustomer(details);
    }

    public void attachService(int id, String service, Date date) throws CustomerNotFoundException, JSONException {
        getCustomer(id).attachService(service, date);
    }

    public List<JSONObject> getServices(int id) throws CustomerNotFoundException{
        return getCustomer(id).getServices();
    }

}

