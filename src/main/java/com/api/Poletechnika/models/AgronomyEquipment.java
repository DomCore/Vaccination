package com.api.Poletechnika.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "agronomy_machinery_equipment")
public class AgronomyEquipment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "parent_id")
    @JsonProperty("parent_id")
    private String parentId;

    @Column(name = "parent_title")
    @JsonProperty("parent_title")
    private String parentTitle;

    @Column(name = "description")
    private String description;

    @JsonProperty("applying_id")
    @Column(name = "applying_id")
    private int applyingId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentTitle() {
        return parentTitle;
    }

    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getApplyingId() {
        return applyingId;
    }

    public void setApplyingId(int applyingId) {
        this.applyingId = applyingId;
    }
}






