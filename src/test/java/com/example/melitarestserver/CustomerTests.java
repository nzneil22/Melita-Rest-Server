package com.example.melitarestserver;

import static org.assertj.core.api.Assertions.assertThat;

import com.melita_task.customerDatabase.Customer;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class CustomerTests {

    @Test
    public void customerObjectSeperateParams() {
        String name = "name";
        String surname = "surname";
        String address = "address";
        Customer customer = new Customer(1, name, surname, address);
        assertThat(customer).isNotNull();
        assertThat(customer.getName()).isEqualTo(name);
        assertThat(customer.getSurname()).isEqualTo(surname);
        assertThat(customer.getAddress()).isEqualTo(address);
    }

    @Test
    public void customerObjectMapParams() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "Customer-Name");
        params.put("surname", "Customer-Surname");
        params.put("address", "Customer-Address");
        Customer customer = new Customer(1, params);
        assertThat(customer).isNotNull();
        assertThat(customer.getName()).isEqualTo(params.get("name"));
        assertThat(customer.getSurname()).isEqualTo(params.get("surname"));
        assertThat(customer.getAddress()).isEqualTo(params.get("address"));
    }

    @Test
    public void customerObjectUpdate() {

        String name = "name";
        String surname = "surname";
        String address = "address";
        Customer customer = new Customer(1, name, surname, address);
        assertThat(customer).isNotNull();

        Map<String, String> params = new HashMap<>();
        params.put("name", "Customer-Name");
        params.put("surname", "Customer-Surname");
        params.put("address", "Customer-Address");
        customer.updateCustomer(params);

        assertThat(customer.getName()).isEqualTo(params.get("name"));
        assertThat(customer.getSurname()).isEqualTo(params.get("surname"));
        assertThat(customer.getAddress()).isEqualTo(params.get("address"));
    }

    Customer customer = new Customer(1, "name", "surname", "address");

    @Test
    public void customerObjectAttachServiceGetService() throws JSONException {
        customer.attachService("MOB_PRE", new Date());
        assertThat(customer.getServices()).isNotEmpty();
    }



}