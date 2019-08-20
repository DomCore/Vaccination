package com.api.Poletechnika.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "agronomy_conditions")
public class AgronomyCondition {

    @Id
    @Column(name = "idcondition")
    @JsonIgnore
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int idcondition;

    @Column(name = "id")
    private String id;

    @Column(name = "title")
    private String title;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public int getIdcondition() {
        return idcondition;
    }

    public void setIdcondition(int idcondition) {
        this.idcondition = idcondition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
