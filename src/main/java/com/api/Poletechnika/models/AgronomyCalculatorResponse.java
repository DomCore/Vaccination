package com.api.Poletechnika.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public class AgronomyCalculatorResponse {
    private Boolean success;
    private Boolean exist;
    private AgronomyCalculatorResponseError error;
    private List<UserCalculationData> data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public AgronomyCalculatorResponseError getError() {
        return error;
    }

    public void setError(AgronomyCalculatorResponseError error) {
        this.error = error;
    }

    public List<UserCalculationData> getData() {
        return data;
    }

    public void setData(List<UserCalculationData> data) {
        this.data = data;
    }

    public Boolean getExist() {
        return exist;
    }

    public void setExist(Boolean exist) {
        this.exist = exist;
    }
}
