package com.example.radioplayer.model;

import java.util.List;

/**
 * http://api.dirble.com/v2/category/5/stations?page=1&per_page=5&token=xxxxx-xxxxxx-xxxxxx
 *
[
    {
        "id":26316,
        "name":"ALLIANCE 92 FM",
        "country":"MG",
        "image":{
            "url":"https://cdn.devality.com/station/26316/LOGO.jpg",
            "thumb":{
                "url":"https://cdn.devality.com/station/26316/thumb_LOGO.jpg"
            }
        },
        "slug":"alliance-92-fm",
        "website":"www.newsmada.com",
        "twitter":"",
        "facebook":"",
        "categories":[
            {
                "id":5,
                "title":"Pop",
                "description":"stations that normally play pop-music",
                "slug":"pop",
                "ancestry":null
            }
        ],
        "streams":[
            {
                "stream":"http://41.188.43.211:8000/alliance92.mp3",
                "bitrate":96,
                "content_type":"audio/mpeg",
                "status":1
            }
        ],
        "created_at":"2016-02-02T08:42:21+01:00",
        "updated_at":"2016-02-02T08:42:22+01:00"
    },
    {
        "id":26298,
        "name":"Intuitive Radio",
        "country":"GB",
        "image":{
            "url":null,
            "thumb":{
                "url":null
            }
        },
        "slug":"intuitive-radio",
        "website":"http://www.intuitiveradio.com",
        "twitter":"",
        "facebook":"",
        "categories":[
            {  }
        ],
        "streams":[
            {
                "stream":"http://live.intuitiveradio.com:8000/1",
                "bitrate":128,
                "content_type":"audio/mpeg",
                "status":1
            }
        ],
        "created_at":"2016-01-30T20:49:55+01:00",
        "updated_at":"2016-01-30T20:49:55+01:00"
    },
    {
        "id":26297,
        "name":"B-Radio",
        "country":"BE",
        "image":{
            "url":"https://cdn.devality.com/station/26297/b-radio-weblogo2015.png",
            "thumb":{
                "url":"https://cdn.devality.com/station/26297/thumb_b-radio-weblogo2015.png"
            }
        },
        "slug":"b-radio",
        "website":"http://www.b-radio.be",
        "twitter":"@bradiobe",
        "facebook":"http://www.facebook.com/b.radio.be",
        "categories":[
            {
                "id":5,
                "title":"Pop",
                "description":"stations that normally play pop-music",
                "slug":"pop",
                "ancestry":null
            }
        ],
        "streams":[
            {
                "stream":"http://s26.myradiostream.com:4396/",
                "bitrate":192,
                "content_type":"audio/mpeg",
                "status":1
            }
        ],
        "created_at":"2016-01-30T20:36:37+01:00",
        "updated_at":"2016-01-30T20:36:38+01:00"
    },
    {
        "id":26279,
        "name":"urbanradio",
        "country":"GB",
            "image":{
            "url":"https://cdn.devality.com/station/26279/imageedit_1_8869024589.png",
            "thumb":{
                "url":"https://cdn.devality.com/station/26279/thumb_imageedit_1_8869024589.png"
            }
        },
        "slug":"urbanradio",
        "website":"http://urban-radio.co.uk/",
        "twitter":"@spwr",
        "facebook":"https://www.facebook.com/SparklyWaveRadio/",
        "categories":[
            {
                "id":5,
                "title":"Pop",
                "description":"stations that normally play pop-music",
                "slug":"pop",
                "ancestry":null
            }
        ],
        "streams":[
            {
                "stream":"http://s6.voscast.com:9688",
                "bitrate":24,
                "content_type":"audio/mpeg",
                "status":1
            }
        ],
        "created_at":"2016-01-28T03:00:46+01:00",
        "updated_at":"2016-01-28T03:00:46+01:00"
    }

]
*/

public class Station {

    private Long id;
    private String name;
    private String country;
    private Image image;
    private String slug;
    private String website;
    private String twitter;
    private String facebook;
    private List<Stream> streams;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public Image getImage() {
        return image;
    }

    public String getSlug() {
        return slug;
    }

    public String getWebsite() {
        return website;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public List<Stream> getStreams() {
        return streams;
    }

    @Override
    public String toString() {
        return String.format("Name %s, country %s", getName(), getCountry());
    }


}
