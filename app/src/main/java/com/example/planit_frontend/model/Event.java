package com.example.planit_frontend.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Event {

    private String id;
    private String name;
    private String description;
    private String location;
    private String creator;
    private String date;



    public Event(String name, String description, String location, String creator, String date) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.creator = creator;
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



}