package com.example.radioplayer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * http://api.dirble.com/v2/categories/primary?token=xxxxx-xxxxxx-xxxxx
 *
[
    {
        "id":44,
        "title":"Misc",
        "description":"Everything else.",
        "slug":"misc",
        "ancestry":null
    },
    {
        "id":5,
        "title":"Pop",
        "description":"stations that normally play pop-music",
        "slug":"pop",
        "ancestry":null
    },
    {
        "id":34,
        "title":"R\u0026B/Urban",
        "description":"",
        "slug":"r-b-urban",
        "ancestry":null
    },
    {
        "id":10,
        "title":"Rap",
        "description":"50 Cent and more.",
        "slug":"rap",
        "ancestry":null
    },
    {
        "id":9,
        "title":"Reggae",
        "description":"Who don't know Bob Marley?",
        "slug":"reggae",
        "ancestry":null
    },
    {
        "id":2,
        "title":"Rock",
        "description":"simple rock. from elvis to metallica and like hardrock as iron maiden.",
        "slug":"rock",
        "ancestry":null
    },
    {
        "id":4,
        "title":"Talk \u0026 Speech",
        "description":"talk \u0026 speech stations like normal talkshows and religous discuss.",
        "slug":"talk-speech",
        "ancestry":null
    },
    ....
]
*/


public class Category {

    // TODO method which looks at title and binds appropriate image
    private Long id;
    private String title;
    private String description;
    private String slug;
    private String ancestry;
    @JsonIgnore
    private Integer icon;

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

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return String.format("Id: %d, Title: %s, Description: %s, Slug: %s, Ancestry: %s",
                getId(), getTitle(), getDescription(), getSlug(), getAncestry());
    }


}
