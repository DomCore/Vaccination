package com.api.Poletechnika.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name = "earth_region_feature")
public class EarthRegionFeature {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id;

    @JsonProperty("region_id")
    @Column(name = "region_id")
    private int regionId;

    @JsonProperty("earth_type_id")
    @Column(name = "earth_type_id")
    private int earthTypeId;

    @JsonProperty("density_title")
    @Column(name = "density_title")
    private String densityTitle;

    @JsonProperty("density_value")
    @Column(name = "density_value")
    private String densityValue;

    @JsonProperty("density_metrics")
    @Column(name = "density_metrics")
    private String densityMetrics;

    @JsonProperty("category_id")
    @Column(name = "category_id")
    private int categoryId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public int getEarthTypeId() {
        return earthTypeId;
    }

    public void setEarthTypeId(int earthTypeId) {
        this.earthTypeId = earthTypeId;
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
