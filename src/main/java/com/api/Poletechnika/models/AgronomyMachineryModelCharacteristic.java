package com.api.Poletechnika.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "agronomy_machinery_characteristics")
public class AgronomyMachineryModelCharacteristic {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "value")
    private String value;

    @Column(name = "id_machinery")
    @JsonIgnore
    private int idMachinery;


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


    public int getIdMachinery() {
        return idMachinery;
    }

    public void setIdMachinery(int idMachinery) {
        this.idMachinery = idMachinery;
    }
}
