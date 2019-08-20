package com.api.Poletechnika.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "reclame")
public class Reclam {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id;

    @JsonIgnore
    @Column(name = "type")
    private String type;

    @Column(name = "title")
    private String title;

    @Column(name = "image")
    private String image;

    @Column(name = "video")
    private String video;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
