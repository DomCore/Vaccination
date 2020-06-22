package com.api.Poletechnika.models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "agronomy_earth_regions")
public class AgronomyEarthRegion {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "title")
    private String title;

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

}
