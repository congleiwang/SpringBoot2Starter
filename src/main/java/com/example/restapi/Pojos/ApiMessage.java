package com.example.restapi.Pojos;

public class ApiMessage {
    private String message;
    private String type;
    private String exeptionMessage;

    public ApiMessage(String message, String type, String exeptionMessage) {
        this.message = message;
        this.type = type;
        this.exeptionMessage = exeptionMessage;
    }

    public ApiMessage() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
