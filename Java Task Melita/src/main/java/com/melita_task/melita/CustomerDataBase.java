package com.melita_task.melita;

import com.melita_task.exceptions.CustomerNotFoundException;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public void attachService(int id, Service service) throws CustomerNotFoundException {
        getCustomer(id).attachService(service);
    }

    public List<String> getServices(int id) throws CustomerNotFoundException{
        return getCustomer(id).getServices().stream().map(Service::getService).collect( Collectors.toList() );
    }
}

