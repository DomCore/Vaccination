package com.api.Poletechnika.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "agronomy_machinery_models")
public class AgronomyMachineryModel {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "type_id")
    private String typeId;

    @Column(name = "type_title")
    private String typeTitle;

    @Column(name = "description")
    private String description;

    @Column(name = "independence")
    private boolean independence;

    @Column(name = "applying_id")
    private int applyingId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isIndependence() {
        return independence;
    }

    public void setIndependence(boolean independence) {
        this.independence = independence;
    }

    public int getApplyingId() {
        return applyingId;
    }

    public void setApplyingId(int applyingId) {
        this.applyingId = applyingId;
    }
}
