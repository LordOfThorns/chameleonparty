package com.example.finalproject.networkapi;

import java.util.ArrayList;

public class FirstResponseObject {
    private String id;
    private String version;
    Urls urls;
    private String created_at;
    private String completed_at;

    public String getStatus() {
        return status;
    }

    private String status;
    private ArrayList<String> output = null;
    private String error = null;
    private String logs = null;

    public class Urls {
        private String get;
        private String cancel;
    }

    public String getLink() {
        return urls.get;
    }

    public ArrayList<String> getOutput() {
        return output;
    }

}
