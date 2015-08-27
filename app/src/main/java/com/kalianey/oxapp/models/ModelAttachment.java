package com.kalianey.oxapp.models;

import java.io.Serializable;

/**
 * Created by kalianey on 18/08/2015.
 */
public class ModelAttachment implements Serializable {

    private static final long serial = 1L;

    String id;
    String thumb;
    String url;
    String description;
    String permalink;


    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



}
