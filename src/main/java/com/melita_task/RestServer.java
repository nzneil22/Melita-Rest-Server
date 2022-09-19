//package com.melita_task;
//
//import com.melita_task.CustomerStructure.FullName;
//import com.melita_task.CustomerStructure.InstallationAddress;
//import com.melita_task.amqp.MessagePayload;
//import com.melita_task.amqp.MessageProducer;
//import com.melita_task.CustomerStructure.Customer;
//import com.melita_task.melita.OrderDatabase;
//import com.melita_task.contract.Service;
//import com.melita_task.melita.Services;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.constraints.NotBlank;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//@SpringBootApplication
//@RestController
//@Slf4j
//public class RestServer {
//
//    private final MessageProducer messageProducer;
//
//    private final OrderDatabase db;
//
//    @Autowired
//    public RestServer(OrderDatabase db, MessageProducer messageProducer) {
//        this.db = db;
//        this.messageProducer = messageProducer;
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(RestServer.class, args);
//    }
//
//
//
//    @PostMapping("/clients")
//    public ResponseEntity<Customer> registerClient(@RequestParam @NotBlank final String name,
//                                                             @RequestParam(required = false) final String middle,
//                                                             @RequestParam @NotBlank final String surname,
//                                                             @RequestParam @NotBlank final String island,
//                                                             @RequestParam @NotBlank final String town,
//                                                             @RequestParam @NotBlank final String street,
//                                                             @RequestParam @NotBlank final String building) {
//        final int id = db.generateId();
//
//        final Customer c = Customer.builder()
//                .id(id)
//                .fullname(
//                        FullName.builder()
//                                .firstName(name)
//                                .middleName(middle)
//                                .lastName(surname)
//                                .build()
//                )
//                .installationAddress(
//                        InstallationAddress.builder()
//                                .island(island)
//                                .town(town)
//                                .street(street)
//                                .building(building)
//                                .build()
//                )
//                .services(new ArrayList<>())
//                .build();
//
//        db.addClient(c);
//        messageProducer.sendMessage(new MessagePayload("Registered new customer", db.getCustomer(db.generateId() - 1)));
//
//        return new ResponseEntity<>(c, HttpStatus.OK);
//    }
//
//    @PutMapping("/clients/{id}")
//    public ResponseEntity<Customer> editClient(@PathVariable final int id,
//                                                         @RequestParam(required = false) @NotBlank final String name,
//                                                         @RequestParam(required = false) final String middle,
//                                                         @RequestParam(required = false) @NotBlank final String surname,
//                                                         @RequestParam(required = false) @NotBlank final String island,
//                                                         @RequestParam(required = false) @NotBlank final String town,
//                                                         @RequestParam(required = false) @NotBlank final String street,
//                                                         @RequestParam(required = false) @NotBlank final String building) {
//
//        db.editCustomer(id, name, middle, surname, island, town, street, building);
//
//        messageProducer.sendMessage(new MessagePayload("Edit existing customer", db.getCustomer(id)));
//
//        return new ResponseEntity<>(db.getCustomer(id), HttpStatus.OK);
//    }
//
//    @GetMapping("/clients/{id}")
//    public ResponseEntity<Customer> getClient(@PathVariable final int id){
//
//        return new ResponseEntity<>(db.getCustomer(id), HttpStatus.OK);
//
//    }
//
//    @PostMapping("/clients/{id}/services")
//    public ResponseEntity<Service> newService(@PathVariable("id") @NotBlank final int id,
//                                              @RequestParam @NotBlank final int serviceId,
//                                              @RequestParam @NotBlank final Services lobtype,
//                                              @RequestParam @NotBlank final String preferredDate,
//                                              @RequestParam @NotBlank final String preferredTime) throws ParseException {
//
//        Date date = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(preferredDate + " " + preferredTime);
//
//        final Service serv = Service.builder().serviceId(serviceId).lobType(lobtype).installationDate(date).build();
//        db.attachService(id, serv);
//
//        return new ResponseEntity<>(serv, HttpStatus.OK);
//    }
//
//    @GetMapping("/clients/{id}/services")
//    public ResponseEntity<List<Service>> getServices(@PathVariable final int id) throws CustomerNotFoundException {
//            return new ResponseEntity<>(db.getCustomer(id).getServices(), HttpStatus.OK);
//    }
//
//}