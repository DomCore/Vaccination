package com.api.Poletechnika.models;

import javax.persistence.*;

@Entity
@Table(name = "videos")
public class Video {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "image")
    private String image;

    @Column(name = "video")
    private String video;

    @Column(name = "description")
    private String description;

    @Column(name = "hashtags")
    private String hashtags;

    @Column(name = "breakdown_id")
    private int breakdownId;

    @Column(name = "breakdown_type")
    private String breakdownType;

    @Column(name = "car_type")
    private String carType;

    @Column(name = "car_model")
    private String carModel;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHashtags() {
        return hashtags;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBreakdownId() {
        return breakdownId;
    }

    public void setBreakdownId(int breakdownId) {
        this.breakdownId = breakdownId;
    }


    public String getBreakdownType() {
        return breakdownType;
    }

    public void setBreakdownType(String breakdownType) {
        this.breakdownType = breakdownType;
    }


    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }
}
