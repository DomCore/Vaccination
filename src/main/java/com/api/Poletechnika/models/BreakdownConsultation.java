package com.api.Poletechnika.models;

import javax.persistence.*;

@Entity
@Table(name = "breakdowns_consultations")
public class BreakdownConsultation {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id;


    @Column(name = "breakdown_id")
    private int breakdownId;

    @Column(name = "user_id")
    private int userId;

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBreakdownId() {
        return breakdownId;
    }

    public void setBreakdownId(int breakdownId) {
        this.breakdownId = breakdownId;
    }
}
