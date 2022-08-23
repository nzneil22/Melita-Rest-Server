package com.example.melitarestserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.melita_task.customerDatabase.Customer;
import com.melita_task.customerDatabase.CustomerDataBase;
import com.melita_task.exceptions.CustomerNotFoundException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class DatabaseTests {

    private CustomerDataBase database = new CustomerDataBase();

    @Test
    public void databaseExists() {
        assertThat(database).isNotNull();
    }

    @Test
    public void addCustomerdeleteCustomer() throws CustomerNotFoundException {
        database.addClient(new Customer(0, "nameOfClient", "surnameOfClient", "addressOfClient"));
        assertThat(database.getCustomer(0)).isNotNull();
        database.deleteCustomer(0);
        assertThrows(CustomerNotFoundException.class, () -> database.getCustomer(0));
    }

    @Test
    public void idIncrement() throws CustomerNotFoundException {
        database.addClient(new Customer(0, "name", "surname", "address"));
        assertThat(database.generateId()).isEqualTo(1);
        database.addClient(new Customer(1, "name", "surname", "address"));
        assertThat(database.generateId()).isEqualTo(2);
        database.deleteCustomer(0);
        database.deleteCustomer(1);
    }

    @Test
    public void editCustomerThroughDatabase() throws CustomerNotFoundException {
        database.addClient(new Customer(0, "name", "surname", "address"));
        assertThat(database.getCustomer(0)).isNotNull();
        assertThat(database.getCustomer(0).getName()).isEqualTo("name");

        Map<String, String> params = new HashMap<>();
        params.put("name", "Customer-Name");
        params.put("surname", "Customer-Surname");
        params.put("address", "Customer-Address");

        database.editCustomer(0, params );
        assertThat(database.getCustomer(0).getName()).isEqualTo("Customer-Name");

        database.deleteCustomer(0);
    }

    @Test
    public void attachServiceThroughDatabase() throws CustomerNotFoundException, JSONException {
        database.addClient(new Customer(0, "name", "surname", "address"));
        assertThat(database.getCustomer(0)).isNotNull();
        assertThat(database.getServices(0)).isEmpty();

        database.attachService(0, "MOB_PRE", new Date());
        assertThat(database.getServices(0)).isNotEmpty();

        database.deleteCustomer(0);
    }

    @Test
    public void getServicesWrongClient(){
        assertThrows(CustomerNotFoundException.class, () -> database.getServices(0));
    }







}