package com.kalianey.oxapp.models;

import java.io.Serializable;

/**
 * Created by kalianey on 11/08/2015.
 */
public class ModelUser implements Serializable {

    private static final long id = 1L;
    private String userId;
    private String name;
    private String avatar_url;
    private String avatar_url_default;
    private String cover_url;
    private Boolean isLoggedInUser;
    private String address;
    private String sex;
    private String age;
    private String email;
    private String distance;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) { this.avatar_url = avatar_url; }

    public String getAvatar_url_default() { return avatar_url_default; }

    public void setAvatar_url_default(String avatar_url_default) { this.avatar_url_default = avatar_url_default; }

    public String getCover_url() {return cover_url;}

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public Boolean getIsLoggedInUser() {
        return isLoggedInUser;
    }

    public void setIsLoggedInUser(Boolean isLoggedInUser) {
        this.isLoggedInUser = isLoggedInUser;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }



//    var sections: NSArray?
//    var questions: NSArray?
//
//    var photos: Array<AnyObject>?
//    var friends: Array<ModelUser>?




}
