package com.api.Poletechnika.models;

import javax.persistence.*;
import java.sql.Date;


@Entity
@Table(name = "license_requests")
public class LicenseRequest {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "status")
    private int status;

    @Column(name = "date")
    private String date;

    @Column(name = "license_term")
    private String licenseTerm;

    @Column(name = "mail")
    private String mail;

    @Column(name = "payment")
    private String payment;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLicenseTerm() {
        return licenseTerm;
    }

    public void setLicenseTerm(String licenseTerm) {
        this.licenseTerm = licenseTerm;
    }
}
