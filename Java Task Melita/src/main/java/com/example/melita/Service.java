package com.example.melita;

public enum Service {

    INTERNET_S("250MBPS Internet"),
    INTERNET_L("1GBPS Internet"),
    TV_S("TV 90 Channels"),
    TV_L("TV 140 Channels"),
    TELE_S("Telephony with Free On net Calls"),
    TELE_L("Telephony with Unlimited Calls"),
    MOB_PRE("Mobile Prepaid"),
    MOB_POST("Mobile Post-paid"),
    ERROR("SERVICE NOT POSSIBLE");

    private String service;

    Service(String service){
        this.service = service;
    }

    public String getService(){
        return service;
    }



}
