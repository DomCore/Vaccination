package com.api.Poletechnika.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

public class EarthRegionFeatureForJSON {

    private int id;
    private int earthTypeId;
    private String earthTypeTitle;
    private String densityTitle;
    private String densityValue;
    private String densityMetrics;
    private int categoryId;
    private String categoryTitle;
    private String categoryName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDensityTitle() {
        return densityTitle;
    }

    public void setDensityTitle(String densityTitle) {
        this.densityTitle = densityTitle;
    }

    public String getDensityValue() {
        return densityValue;
    }

    public void setDensityValue(String densityValue) {
        this.densityValue = densityValue;
    }

    public String getDensityMetrics() {
        return densityMetrics;
    }

    public void setDensityMetrics(String densityMetrics) {
        this.densityMetrics = densityMetrics;
    }

    public int getEarthTypeId() {
        return earthTypeId;
    }

    public void setEarthTypeId(int earthTypeId) {
        this.earthTypeId = earthTypeId;
    }

    public String getEarthTypeTitle() {
        return earthTypeTitle;
    }

    public void setEarthTypeTitle(String earthTypeTitle) {
        this.earthTypeTitle = earthTypeTitle;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
