package com.api.Poletechnika.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "earth_type_characteristics")
public class AgronomyEarthTypeCharacteristic {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "title")
    private String title;

    @Column(name = "value")
    private String value;

    @Column(name = "metrics")
    private String metrics;

    @Column(name = "earth_type_id")
    @JsonIgnore
    private int earthTypeId;


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


    public int getEarthTypeId() {
        return earthTypeId;
    }

    public void setEarthTypeId(int earthTypeId) {
        this.earthTypeId = earthTypeId;
    }

    public String getMetrics() {
        return metrics;
    }

    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
