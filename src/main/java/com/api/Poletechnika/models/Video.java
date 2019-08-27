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

    @Column(name = "filters")
    private String filters;

    @Column(name = "breakdown_id")
    private int breakdownId;


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

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
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
}
