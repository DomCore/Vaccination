package com.api.Poletechnika.models;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;


public class UserCalculationAllData {

    private int id;

    private int number;

    private String title;

    private List<CalculationAreaItem> area;

    private String description;

    private String date;

    private List<UserCalculationData> input_data;

    private List<UserCalculationData> output_data;

    @JsonIgnore
    private int userId;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<CalculationAreaItem> getArea() {
        return area;
    }

    public void setArea(List<CalculationAreaItem> area) {
        this.area = area;
    }


    public List<UserCalculationData> getInput_data() {
        return input_data;
    }

    public void setInput_data(List<UserCalculationData> input_data) {
        this.input_data = input_data;
    }

    public List<UserCalculationData> getOutput_data() {
        return output_data;
    }

    public void setOutput_data(List<UserCalculationData> output_data) {
        this.output_data = output_data;
    }
}

