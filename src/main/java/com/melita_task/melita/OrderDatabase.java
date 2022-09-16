package com.melita_task.melita;

import com.melita_task.CustomerStructure.Customer;
import com.melita_task.exceptions.CustomerNotFoundException;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class OrderDatabase {
    List<Customer> customers = new ArrayList<>();

    public void addClient(Customer c) {
        customers.add(c);
    }

    public int generateId(){
        if(customers.isEmpty()) return 0;
        return customers.get(customers.size()-1).getId()+1;
    }

    public void deleteCustomer(final int id) throws CustomerNotFoundException {
        getCustomer(id);
        customers = customers.stream()
                .filter(c -> c.getId() != id)
                .collect(Collectors.toList());
    }

    public Customer getCustomer(final int id) throws CustomerNotFoundException {
        return customers.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public void editCustomer(final int id,
                             final String first_name,
                             final String middle_name,
                             final String last_name,
                             final String island,
                             final String town,
                             final String street,
                             final String building) throws CustomerNotFoundException {

        getCustomer(id).updateCustomer(first_name, middle_name, last_name, island, town, street, building);
    }

    public void attachService(final int id, final Service service) throws CustomerNotFoundException{
        getCustomer(id).attachService(service);
    }
}

