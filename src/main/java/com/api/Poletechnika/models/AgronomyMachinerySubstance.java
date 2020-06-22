package com.api.Poletechnika.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name = "agronomy_machinery_substances")
public class AgronomyMachinerySubstance {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "title")
    private String title;

    @JsonProperty("applying_id")
    @Column(name = "applying_id")
    private int applyingId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getApplyingId() {
        return applyingId;
    }

    public void setApplyingId(int applyingId) {
        this.applyingId = applyingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
