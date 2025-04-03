package com.example.planit_frontend.model;

public class Event {

    private String id;
    private String name;
    private String description;
    private String location;
    private String creatorEmail;
    private String date;


    // full constructor
    public Event(String name, String description, String location, String creatorEmail, String date) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.creatorEmail = creatorEmail;
        this.date = date;
    }


    // Constructor that takes update fields
    public Event(String name, String description, String location, String date) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



}