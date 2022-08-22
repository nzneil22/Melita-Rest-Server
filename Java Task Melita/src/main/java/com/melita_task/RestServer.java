package com.melita_task;

import com.melita_task.exceptions.InvalidParamsException;
import com.melita_task.melita.Customer;
import com.melita_task.melita.CustomerDataBase;
import com.melita_task.melita.Service;
import com.melita_task.amqp.MessagePayload;
import com.melita_task.amqp.MessageProducer;
import com.melita_task.exceptions.CustomerNotFoundException;
import com.melita_task.exceptions.ServiceNotFoundException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Map;
import java.util.stream.Collectors;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
@RestController
public class RestServer {


    private final MessageProducer messageProducer;

    private final CustomerDataBase db;

    @Autowired
    public RestServer(CustomerDataBase db, MessageProducer messageProducer) {
        this.db = db;
        this.messageProducer = messageProducer;
    }

    public static void main(String[] args) {
        SpringApplication.run(RestServer.class, args);
    }

    @GetMapping(UriConstants.REGISTER_CLIENT)
    public ResponseEntity<String> registerClient(@RequestParam Map<String, String> userDetails){
        try {
            verifyUserDetails(userDetails, true);
            db.addClient(new Customer(db.generateId(), userDetails));
            messageProducer.send(new MessagePayload("Registered new customer", db.getCustomer(db.generateId() - 1)));
            return new ResponseEntity<>(new JSONObject(db.getCustomer(db.generateId()-1).getMap()).toString(), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>("Exception happened\n"+e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(UriConstants.EDIT_CLIENT)
    public ResponseEntity<String> editClient(@RequestParam int id, @RequestParam Map<String, String> userDetails){
        try {
            verifyUserDetails(userDetails, false);
            db.editCustomer(id, userDetails);
            messageProducer.send( new MessagePayload("Edit existing customer", db.getCustomer(id)));
            return new ResponseEntity<>(new JSONObject(db.getCustomer(id).getMap()).toString(), HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>("Exception happened\n"+e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(UriConstants.ATTACH_SERVICE)
    public ResponseEntity<String> newService(@RequestParam Map<String, Object> params){
        try {

            verifyAttachService(params);
            int id = Integer.parseInt((String) params.get("id"));
            Service service = Service.convert((String) params.get("service"));
            Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(params.get("preferredDate") +" "+params.get("preferredTime"));

            if (service == Service.ERROR) throw new ServiceNotFoundException("Selected Service is not available");
            db.attachService(id, service, date);
            messageProducer.send( new MessagePayload("Attach Service: "+service.getService(), db.getCustomer(id)));

            return new ResponseEntity<>(new JSONObject(db.getCustomer(id).getMap()).toString(), HttpStatus.OK);
        } catch(CustomerNotFoundException | InvalidParamsException | JSONException | ServiceNotFoundException e) {
            return new ResponseEntity<>("Exception happened\n"+e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch(ParseException | NullPointerException e){
            return new ResponseEntity<>("Exception happened whilst parsing date/time\n"+e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(UriConstants.CLIENT_SERVICES)
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

    private void verifyAttachService(Map<String, Object> serviceDetails) throws InvalidParamsException {
        Boolean valid = serviceDetails.containsKey("id") && serviceDetails.containsKey("service") && serviceDetails.containsKey("preferredDate") && serviceDetails.containsKey("preferredTime");

        if(!valid){ throw new InvalidParamsException("Invalid Parameters entered. id, service, preferredDate and preferredTime must be present"); }

    }
}