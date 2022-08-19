package com.example.melita;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class MelitaApplication {

    CustomerDataBase db = new CustomerDataBase();

    public static void main(String[] args) {
        SpringApplication.run(MelitaApplication.class, args);
    }

    @GetMapping("/registerClient")
    public String registerClient(@RequestParam(value="name") String name,
                           @RequestParam(value="surname") String surname,
                           @RequestParam(value="address") String address) {

        db.addClient(new Customer(db.generateId(), name, surname, address));

//        Boolean verification = verifyRegistrationParams(params);
//        if(verification){ return "Parameters are " +params.entrySet(); }
        return "Customers: " +db.getCustomers();
    }

    @GetMapping("/editClient")
    public String editClient(@RequestParam int id, @RequestParam Map<String, String> params){
        try {
            db.editCustomer(id, params);
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