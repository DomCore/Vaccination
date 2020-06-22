package com.api.Poletechnika.models;

import java.util.List;

public class AgronomyCalculatorResponseError {

    private String source;

    private List<AgronomyCalculatorResponseErrorInfo> info = null;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<AgronomyCalculatorResponseErrorInfo> getInfo() {
        return info;
    }

    public void setInfo(List<AgronomyCalculatorResponseErrorInfo> info) {
        this.info = info;
    }

}