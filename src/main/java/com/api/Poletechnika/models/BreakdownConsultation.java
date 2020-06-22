package com.api.Poletechnika.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    @JsonProperty("is_new")
    @Column(name = "is_new")
    private int isNew;


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
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Date dateF = null;
        try {
            dateF = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(dateF != null){
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            return outputFormat.format(dateF);
        }else {
            return date;
        }

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


    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }
}
