package com.melita_task.amqp;

import com.melita_task.melita.Customer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

@RequiredArgsConstructor
@Data
public class MessagePayload {
    final String alteration;
    final Customer customer;

    @Override
    public String toString(){
        return alteration + ": "+new JSONObject(customer.getMap());
    }
}
