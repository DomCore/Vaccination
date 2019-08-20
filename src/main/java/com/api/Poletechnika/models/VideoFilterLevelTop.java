package com.api.Poletechnika.models;

import javax.persistence.*;
import java.util.List;

public class VideoFilterLevelTop {

    private int id;

    private String title;

    private List<VideoFilter> items = null;


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


    public List<VideoFilter> getItems() {
        return items;
    }

    public void setItems(List<VideoFilter> items) {
        this.items = items;
    }
}
