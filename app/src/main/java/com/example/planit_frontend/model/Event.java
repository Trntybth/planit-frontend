package com.example.planit_frontend.model;

import java.time.LocalDate;
import java.util.List;

public class Event {

    private String id;
    private String name;
    private String description;
    private String location;
    private Organisation creator;
    private LocalDate date;
    private List<Member> attendees;

}
