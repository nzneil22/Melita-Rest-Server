package com.melita_task;

import com.melita_task.melita.Customer;
import com.melita_task.melita.CustomerDataBase;
import com.melita_task.melita.Service;
import com.melita_task.amqp.MessagePayload;
import com.melita_task.amqp.MessageProducer;
import com.melita_task.exceptions.CustomerNotFoundException;
import com.melita_task.exceptions.ServiceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@SpringBootApplication
@RestController
public class RestServer {

    @Autowired
    MessageProducer messageProducer;

    CustomerDataBase db = new CustomerDataBase();

    public static void main(String[] args) {
        SpringApplication.run(RestServer.class, args);
    }

    @GetMapping("/registerClient")
    public String registerClient(@RequestParam(value="name") String name,
                           @RequestParam(value="surname") String surname,
                           @RequestParam(value="address") String address) throws CustomerNotFoundException {

        db.addClient(new Customer(db.generateId(), name, surname, address));

        messageProducer.send( new MessagePayload("register new customer", db.getCustomer(db.generateId()-1)));

        return "Customers: " +db.getCustomers();
    }

    @GetMapping("/editClient")
    public String editClient(@RequestParam int id, @RequestParam Map<String, String> params){
        try {
            db.editCustomer(id, params);

            messageProducer.send( new MessagePayload("edit existing customer", db.getCustomer(id)));

            return "Customers: " +db.getCustomers();
        } catch(Exception e) {
            return "ERROR: "+e.getMessage();
        }
    }

    @GetMapping("/attachService")
    public String newService(@RequestParam int id, @RequestParam Service service){
        try {
            if (service == Service.ERROR) throw new ServiceNotFoundException("Selected Service is not available");
            db.attachService(id, service);
            messageProducer.send( new MessagePayload("Attach Service: "+service.getService(), db.getCustomer(id)));
            return "Customer: " +db.getCustomer(id);
        } catch(CustomerNotFoundException e) {
            return "ERROR: "+e.getMessage();
        } catch(ServiceNotFoundException e) {
            return "ERROR: "+e.getMessage();
        }
    }


    @GetMapping("/clientServices")
    public String clientServices(@RequestParam int id){
        try {
            return "Customer "+id+" services: " +db.getServices(id);
        } catch(CustomerNotFoundException e) {
            return "ERROR: "+e.getMessage();
        }
    }


}