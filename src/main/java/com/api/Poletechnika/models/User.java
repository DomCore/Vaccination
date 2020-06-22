package com.api.Poletechnika.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "mail")
    private String mail;

    @Column(name = "website")
    private String website;

    @Column(name = "registry")
    private String registry;

    @Column(name = "company")
    private String company;

    @Column(name = "password")
    private String pass;

    @Column(name = "registration_status")
    private String registrationStatus;

    @Column(name = "registration_date")
    private String registration_date;

    @JsonIgnore
    @Column(name = "license_message")
    private String license_message;

    @Column(name = "token")
    private String token;

    @Column(name = "token_date")
    private String token_date;

    @Column(name = "license")
    private String license;

    @Column(name = "license_term")
    private String license_term;

    @JsonIgnore
    @Column(name = "change_code")
    private String change_code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getRegistration_date() {
        return registration_date;
    }

    public void setRegistration_date(String registration_date) {
        this.registration_date = registration_date;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken_date() {
        return token_date;
    }

    public void setToken_date(String token_date) {
        this.token_date = token_date;
    }


    public String getLicense_term() {
        return license_term;
    }

    public void setLicense_term(String license_term) {
        this.license_term = license_term;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLicense_message() {
        return license_message;
    }

    public void setLicense_message(String license_message) {
        this.license_message = license_message;
    }

    public String getChange_code() {
        return change_code;
    }

    public void setChange_code(String change_code) {
        this.change_code = change_code;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }
}
