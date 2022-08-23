package com.example.melitarestserver;

import com.fasterxml.classmate.Annotations;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class RestTests {

    private final String restServerPort = "9095";
    private final RequestSpecification httpRequest = RestAssured.given().auth().basic("melita_task", "12345");
    private int lastId;

    @Test
    public void registerClient() throws JSONException {
        String clientName = "Test";
        String clientSurname = "Client";
        String clientAddress = "Test Client";
        String url = "http://127.0.0.1:"+restServerPort+"/registerClient?name="+clientName+"&surname="+clientSurname+"&address="+clientAddress;

        ResponseBody body = httpRequest.get(url).body();

        //Converting the response body to string
        JSONObject rbdy = new JSONObject(body.asString());
        System.out.println("Received: "+rbdy);
        assertThat(rbdy.get("name")).isEqualTo(clientName);
        assertThat(rbdy.get("surname")).isEqualTo(clientSurname);
        assertThat(rbdy.get("address")).isEqualTo(clientAddress);
    }

    @Test
    public void editClient() throws JSONException {
        String clientName = "Edited";
        String clientSurname = "Client";
        String clientAddress = "Edited Client";
        String url = "http://127.0.0.1:"+restServerPort+"/editClient?id="+getLastId()+"&name="+clientName+"&surname="+clientSurname+"&address="+clientAddress;

        ResponseBody body = httpRequest.get(url).body();

        //Converting the response body to string

        JSONObject rbdy = new JSONObject(body.asString());
        assertThat(rbdy.get("name")).isEqualTo(clientName);
        assertThat(rbdy.get("surname")).isEqualTo(clientSurname);
        assertThat(rbdy.get("address")).isEqualTo(clientAddress);

    }

    @Test
    public void clientServices() {
        String url = "http://127.0.0.1:"+restServerPort+"/clientServices";

        ResponseBody body = httpRequest.get(url).body();

        String rbdy = body.asString();
        assertThat(rbdy).isNotNull();
    }

    @Test
    public void attachService() throws JSONException {
        String service = "MOB_POST";
        String preferredDate = "23/08/2022";
        String preferredTime = "10:00:00";
        String url = "http://127.0.0.1:"+restServerPort+"/attachService?id="+getLastId()+"&service=MOB_POST&preferredDate="+preferredDate+"&preferredTime="+preferredTime;
        ResponseBody body = httpRequest.get(url).body();

        JSONObject rbdy = new JSONObject(body.asString());
        JSONArray services = new JSONArray(rbdy.get("services").toString());
        JSONObject lastService = (JSONObject) services.get(services.length() -1);
        assertThat(lastService.get("Service")).isEqualTo("Mobile Post-paid");
        assertThat(lastService.get("PreferredInstallationDateTime")).isEqualTo(preferredDate + " " +preferredTime);

    }

    @Test
    public void attachInvalidService() throws JSONException {
        String service = "INVALID_SERVICE";
        String preferredDate = "23/08/2022";
        String preferredTime = "10:00:00";
        String url = "http://127.0.0.1:"+restServerPort+"/attachService?id="+getLastId()+"&service="+service+"&preferredDate="+preferredDate+"&preferredTime="+preferredTime;
        ResponseBody body = httpRequest.get(url).body();

        assertThat(body.asString()).contains("Selected Service is not available");
    }

    @Test
    public void attachServiceInvalidDate() throws JSONException {
        String service = "MOB_POST";
        String preferredDate = "INVALID_DATE";
        String preferredTime = "10:00:00";
        String url = "http://127.0.0.1:"+restServerPort+"/attachService?id="+getLastId()+"&service="+service+"&preferredDate="+preferredDate+"&preferredTime="+preferredTime;
        ResponseBody body = httpRequest.get(url).body();

        assertThat(body.asString()).contains("Exception happened whilst parsing date/time (Date format must match the following: dd/MM/yyyy)");
    }

    @Test
    public void attachServiceInvalidTime() throws JSONException {
        String service = "MOB_POST";
        String preferredDate = "23/08/2022";
        String preferredTime = "INVALID_TIME";
        String url = "http://127.0.0.1:"+restServerPort+"/attachService?id="+getLastId()+"&service="+service+"&preferredDate="+preferredDate+"&preferredTime="+preferredTime;
        ResponseBody body = httpRequest.get(url).body();

        assertThat(body.asString()).contains("Exception happened whilst parsing date/time (Date format must match the following: dd/MM/yyyy)");
    }

    private int getLastId() throws JSONException {
        String url = "http://127.0.0.1:"+restServerPort+"/clientServices";
        Response res = httpRequest.get(url);
        ResponseBody body = res.body();

        JSONObject lastClient;

        try {
            JSONArray rbdy = new JSONArray(body.asString());
            lastClient = (JSONObject) rbdy.get(rbdy.length() - 1);
        } catch (JSONException e){
            lastClient = new JSONObject(body.asString());
        }
        return (int) lastClient.get("id");
    }

}
