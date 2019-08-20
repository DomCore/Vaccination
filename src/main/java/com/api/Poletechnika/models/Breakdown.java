package com.api.Poletechnika.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "breakdowns")
public class Breakdown {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id;


    @Column(name = "code")
    private int code;

    @Column(name = "unit")
    private int unit;

    @Column(name = "title")
    private String title;

    @Column(name = "image")
    private String image;

    @Column(name = "car_id")
    private int car_id;

    @Column(name = "description")
    private String description;

    @Column(name = "manual")
    private String manual;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCar_id() {
        return car_id;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManual() {
        return manual;
    }

    public void setManual(String manual) {
        this.manual = manual;
    }
}
