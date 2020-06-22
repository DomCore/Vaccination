package com.api.Poletechnika.models;

import javax.persistence.Column;
import java.util.List;

public class AgronomyMachineryModelForJSON {


    private int id;
    private String title;
    private String type_id;
    private String type_title;
    private String description;
    private boolean independence;
    private int applying_id;
    private String applying_title;
    private String applying_name;
    private List<MachineryCharacteristicGeneral> characteristics_general;
    private List<MachineryCharacteristicSimple> characteristics_simple;


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

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getType_title() {
        return type_title;
    }

    public void setType_title(String type_title) {
        this.type_title = type_title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public List<MachineryCharacteristicGeneral> getCharacteristicsGeneral() {
        return characteristics_general;
    }

    public void setCharacteristicsGeneral(List<MachineryCharacteristicGeneral> characteristics_general) {
        this.characteristics_general = characteristics_general;
    }

    public List<MachineryCharacteristicSimple> getCharacteristicsSimple() {
        return characteristics_simple;
    }

    public void setCharacteristicsSimple(List<MachineryCharacteristicSimple> characteristics_simple) {
        this.characteristics_simple = characteristics_simple;
    }

    public boolean isIndependence() {
        return independence;
    }

    public void setIndependence(boolean independence) {
        this.independence = independence;
    }


    public int getApplying_id() {
        return applying_id;
    }

    public void setApplying_id(int applying_id) {
        this.applying_id = applying_id;
    }


    public String getApplying_title() {
        return applying_title;
    }

    public void setApplying_title(String applying_title) {
        this.applying_title = applying_title;
    }

    public String getApplying_name() {
        return applying_name;
    }

    public void setApplying_name(String applying_name) {
        this.applying_name = applying_name;
    }
}
