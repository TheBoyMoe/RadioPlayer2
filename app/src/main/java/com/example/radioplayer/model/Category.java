package com.example.radioplayer.model;


public class Category {

    private Long id;
    private String title;
    private String description;
    private String slug;
    private String ancestry;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSlug() {
        return slug;
    }

    public String getAncestry() {
        return ancestry;
    }

    @Override
    public String toString() {
        return getTitle();
    }


}
