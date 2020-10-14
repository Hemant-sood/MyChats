package com.example.mychats;

import java.util.jar.Attributes;

public class ReceivedRequestModel {

    private String Name;

    public ReceivedRequestModel() {

    }

    public ReceivedRequestModel(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
