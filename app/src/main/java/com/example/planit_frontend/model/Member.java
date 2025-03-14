package com.example.planit_frontend.model;


import java.util.List;

public class Member extends User {
    private List<Event> eventsList; public


    // getter setter
    List<Event> getEventsList() {
        return eventsList;
    }
    public void setEventsList(List<Event> eventsList) {
        this.eventsList = eventsList;
    }

    // constructor
    public Member(String googleId, String email, String username) {

    }



}
