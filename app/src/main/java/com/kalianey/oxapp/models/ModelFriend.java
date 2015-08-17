package com.kalianey.oxapp.models;

import java.io.Serializable;

/**
 * Created by kalianey on 14/08/2015.
 */
public class ModelFriend implements Serializable {

    private static final long sId = 1L;

    private String opponentId;
    private String displayName;
    private String profileUrl;
    private String avatarUrl;
    private String coverUrl;
    private Boolean isFriend;
    private String status;
    private Long lastMessageTimestamp;
    private Boolean wasCorrespondence;
    private String convId;
    private String address;
    private String sex;
    private String age;

    public String getOpponentId() {
        return opponentId;
    }

    public void setOpponentId(String opponentId) {
        this.opponentId = opponentId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Boolean getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(Boolean isFriend) {
        this.isFriend = isFriend;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Long lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public Boolean getWasCorrespondence() {
        return wasCorrespondence;
    }

    public void setWasCorrespondence(Boolean wasCorrespondence) {
        this.wasCorrespondence = wasCorrespondence;
    }

    public String getConvId() {
        return convId;
    }

    public void setConvId(String convId) {
        this.convId = convId;
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



}
