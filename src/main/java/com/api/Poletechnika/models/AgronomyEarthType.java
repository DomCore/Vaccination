package com.api.Poletechnika.models;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "agronomy_earth_types")
@JsonFilter("SomeBeanFilterEarth")
public class AgronomyEarthType {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "title")
    private String title;

    //@Column(name = "regions_id")
    //private String regions_id;


    @Transient
    private List<AgronomyEarthTypeCharacteristic> characteristics;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<AgronomyEarthTypeCharacteristic> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(List<AgronomyEarthTypeCharacteristic> characteristics) {
        this.characteristics = characteristics;
    }
}
