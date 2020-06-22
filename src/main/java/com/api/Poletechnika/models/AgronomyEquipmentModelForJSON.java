package com.api.Poletechnika.models;

import java.util.List;

public class AgronomyEquipmentModelForJSON {


    private int id;
    private String title;
    private String parent_id;
    private String parent_title;
    private String description;
    private int applying_id;
    private String applying_title;
    private String applying_name;
    private List<EquipmentCharacteristicGeneral> characteristics_general;
    private List<EquipmentCharacteristicSimple> characteristics_simple;


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public List<EquipmentCharacteristicGeneral> getCharacteristicsGeneral() {
        return characteristics_general;
    }

    public void setCharacteristicsGeneral(List<EquipmentCharacteristicGeneral> characteristics_general) {
        this.characteristics_general = characteristics_general;
    }

    public List<EquipmentCharacteristicSimple> getCharacteristicsSimple() {
        return characteristics_simple;
    }

    public void setCharacteristicsSimple(List<EquipmentCharacteristicSimple> characteristics_simple) {
        this.characteristics_simple = characteristics_simple;
    }

    public String getParentId() {
        return parent_id;
    }

    public void setParentId(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getParentTitle() {
        return parent_title;
    }

    public void setParentTitle(String parent_title) {
        this.parent_title = parent_title;
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
