package com.api.Poletechnika.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "user_calculations_data")
public class UserCalculationData {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "title")
    private String title;


    @Column(name = "value")
    private String value;

    @JsonIgnore
    @Column(name = "id_calculation")
    private int idCalculation;

    @JsonIgnore
    @Column(name = "type")
    private String type ;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public int getIdCalculation() {
        return idCalculation;
    }

    public void setIdCalculation(int idCalculation) {
        this.idCalculation = idCalculation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

