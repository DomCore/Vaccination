package com.api.Poletechnika.models;

import javax.persistence.*;
import java.util.List;

public class AgronomyMachineryModelForJSON {


    private int id;
    private String title;
    private String type_id;
    private String type_title;
    private String description;
    private List<AgronomyMachineryModelCharacteristic> characteristics;


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

    public List<AgronomyMachineryModelCharacteristic> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(List<AgronomyMachineryModelCharacteristic> characteristics) {
        this.characteristics = characteristics;
    }
}
