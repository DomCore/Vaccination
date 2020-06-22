package com.api.Poletechnika.models;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "breakdown_filters")
@JsonFilter("SomeBeanFilter")
public class BreakdownFilter {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "type")
    private String type;

    @JsonIgnore
    @Column(name = "value_id")
    private String valueId;

    @JsonIgnore
    @Column(name = "value_parent")
    private String value_parent;


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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    public String getValue_parent() {
        return value_parent;
    }

    public void setValue_parent(String value_parent) {
        this.value_parent = value_parent;
    }
}
