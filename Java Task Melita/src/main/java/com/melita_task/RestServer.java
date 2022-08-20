package com.melita_task;

import com.melita_task.exceptions.InvalidParamsException;
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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    public String registerClient(@RequestParam Map<String, String> userDetails){

        try {
            verifyUserDetails(userDetails, true);
            db.addClient(new Customer(db.generateId(), userDetails));

            messageProducer.send(new MessagePayload("register new customer", db.getCustomer(db.generateId() - 1)));

            return "Customers: " + db.getCustomers();
        }catch(Exception e){
            return "ERROR: "+e.getMessage();
        }
    }

    @GetMapping("/editClient")
    public String editClient(@RequestParam int id, @RequestParam Map<String, String> userDetails){
        try {
            verifyUserDetails(userDetails, false);
            db.editCustomer(id, userDetails);

            messageProducer.send( new MessagePayload("edit existing customer", db.getCustomer(id)));

            return "Customers: " +db.getCustomers();
        } catch(Exception e) {
            return "ERROR: "+e.getMessage();
        }
    }

    @GetMapping("/attachService")
    public String newService(@RequestParam Map<String, Object> params){
        try {

            int id = Integer.parseInt((String) params.get("id"));
            Service service = Service.convert((String) params.get("service"));


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
    public String clientServices(@RequestParam Map<String, String> params){
        try {
            if(params.containsKey("id")){
                int id = Integer.parseInt(params.get("id"));
                return "Customer "+id+" services: " +db.getServices(id);
            }
            if(db.getCustomers().isEmpty()) throw new CustomerNotFoundException("There are no customers yet.");

            return "Customer services: " +db.getCustomers().stream()
                    .map(Customer::getServiceWithId).collect(Collectors.toList());

        } catch(CustomerNotFoundException e) {
            return "ERROR: "+e.getMessage();
        }
    }

    private void verifyUserDetails(Map<String, String> userDetails, Boolean all) throws InvalidParamsException {
        Boolean valid;
        if(all){valid = userDetails.containsKey("name") && userDetails.containsKey("surname") && userDetails.containsKey("address");}
        else{ valid = userDetails.containsKey("name") || userDetails.containsKey("surname") || userDetails.containsKey("address");}

        if (!valid && all){ throw new InvalidParamsException("Invalid Parameters entered. name, surname and address must all be present");}
        else if(!valid){ throw new InvalidParamsException("Invalid Parameters entered. At least one of name, surname or address must be present"); }

    }

    private void verifyAttachService(Map<String, Object> serviceDetails, Boolean all) throws InvalidParamsException {
        Boolean valid;
        if(all){valid = serviceDetails.containsKey("name") && serviceDetails.containsKey("surname") && serviceDetails.containsKey("address");}
        else{ valid = serviceDetails.containsKey("name") || serviceDetails.containsKey("surname") || serviceDetails.containsKey("address");}

        if (!valid && all){ throw new InvalidParamsException("Invalid Parameters entered. name, surname and address must all be present");}
        else if(!valid){ throw new InvalidParamsException("Invalid Parameters entered. At least one of name, surname or address must be present"); }

    }
}