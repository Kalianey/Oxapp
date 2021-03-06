package com.kalianey.oxapp.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalianey on 11/08/2015.
 */
public class ModelUser implements Serializable, ClusterItem {

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
    private List<ModelQuestion> questions;
    private JSONArray sections;
    private List<ModelUser> friends = new ArrayList<ModelUser>();
    //private List<ModelAttachment> photos = new ArrayList<ModelAttachment>();
    private ArrayList<ModelAttachment> photos = new ArrayList<ModelAttachment>();


//    public List<ModelAttachment> getPhotos() {
//        return photos;
//    }
//
//    public void setPhotos(List<ModelAttachment> photos) {
//        this.photos = photos;
//    }

    public ArrayList<ModelAttachment> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<ModelAttachment> photos) {
        this.photos = photos;
    }

    public List<ModelUser> getFriends() {
        return friends;
    }

    public void setFriends(List<ModelUser> friends) {
        this.friends = friends;
    }

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
        if (this.avatar_url == null) {
            return this.avatar_url_default;
        }
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) { this.avatar_url = avatar_url; }

    //public String getAvatar_url_default() { return avatar_url_default; }

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


    public JSONArray getSections() {
        return sections;
    }

    public void setSections(JSONArray sections) {
        this.sections = sections;
    }


    public List<ModelQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<ModelQuestion> questions) {
        this.questions = questions;
    }



    /** LOCATION **/
    private double lat;
    private double lng;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }


    @Override
    public LatLng getPosition() {
        return new LatLng(lat, lng);
    }
}
