package com.api.Poletechnika.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class AgronomyCalculatorRequest {

    public String machinery_qty;
    public String applying;
    public String work_width;
    public String fuel_per_hour;
    public String fuel_tank_volume;
    public String work_area;
    public String priming_type;
    public String weather;


    public String getWork_width() {
        return work_width;
    }

    public void setWork_width(String work_width) {
        this.work_width = work_width;
    }

    public String getWork_area() {
        return work_area;
    }

    public void setWork_area(String work_area) {
        this.work_area = work_area;
    }

    public String getPriming_type() {
        return priming_type;
    }

    public void setPriming_type(String priming_type) {
        this.priming_type = priming_type;
    }

    public String getFuel_per_hour() {
        return fuel_per_hour;
    }

    public void setFuel_per_hour(String fuel_per_hour) {
        this.fuel_per_hour = fuel_per_hour;
    }

    public String getFuel_tank_volume() {
        return fuel_tank_volume;
    }

    public void setFuel_tank_volume(String fuel_tank_volume) {
        this.fuel_tank_volume = fuel_tank_volume;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getMachinery_qty() {
        return machinery_qty;
    }

    public void setMachinery_qty(String machinery_qty) {
        this.machinery_qty = machinery_qty;
    }

    public String getApplying() {
        return applying;
    }

    public void setApplying(String applying) {
        this.applying = applying;
    }
}
