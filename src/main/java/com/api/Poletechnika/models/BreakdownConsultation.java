package com.api.Poletechnika.models;

import javax.persistence.*;

@Entity
@Table(name = "breakdowns")
public class BreakdownConsultation {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id;


    @Column(name = "breakdown_id")
    private int breakdown_id;

    @Column(name = "user_id")
    private int user_id;

    @Column(name = "message")
    private String message;

    @Column(name = "date")
    private String date;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getBreakdown_id() {
        return breakdown_id;
    }

    public void setBreakdown_id(int breakdown_id) {
        this.breakdown_id = breakdown_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
