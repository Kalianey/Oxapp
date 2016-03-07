package com.kalianey.oxapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by kalianey on 10/08/2015.
 */

//ModelConversation m = ;
//m.setFoo().setBla();
//m.setBla()

public class ModelConversation implements Serializable {

    @SerializedName("conversationId")
    private String id;
    private String opponentId;
    private String initiatorId;
    @SerializedName("displayName")
    private String name;
    private String avatarUrl;
    private String previewText;
    private String timeLabel;
    private String conversationViewed;
    private String conversationRead;
    @SerializedName("reply")
    private String conversationReplied;
    @SerializedName("onlineStatus")
    private String onlineStatus;

    private Boolean onlineStatusBool;
    private String newMessageCount;

    //private List<ModelConversation> conv ; //initialized with a null

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(String initiatorId) {
        this.initiatorId = initiatorId;
    }


    public String getOpponentId() {
        return opponentId;
    }
    public void setOpponentId(String opponentId) {
        this.opponentId = opponentId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl(){
        return avatarUrl;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getPreviewText(){
        return previewText;
    }
    public void setPreviewText(String previewText) {
        this.previewText = previewText;
    }

    public String getTimeLabel() {
        return timeLabel;
    }
    public void setTimeLabel(String timeLabel){
        this.timeLabel = timeLabel;
    }

    public String getConversationViewed() {
        return conversationViewed;
    }
    public void setConversationViewed(String conversationViewed) {
        this.conversationViewed = conversationViewed;
    }

    public String getConversationRead() {
        return conversationRead;
    }
    public void setConversationRead(String conversationRead){
        this.conversationRead = conversationRead;
    }

    public String getconversationReplied() {
        return conversationReplied;
    }
    public void setconversationReplied(String conversationReplied){
        this.conversationReplied = conversationReplied;
    }

    public String getonlineStatus() {
        return onlineStatus;
    }
    public void setonlineStatus(String onlineStatus){
        this.onlineStatus = onlineStatus;
    }

    public Boolean getOnlineStatusBool() {
        if (this.onlineStatus.equals("1"))
        {
            this.onlineStatusBool = true;
        } else {
            this.onlineStatusBool = false;
        }

        return  this.onlineStatusBool;
    }

    public void setOnlineStatusBool(Boolean onlineStatusBool) {
        if (this.onlineStatus.equals("1"))
        {
            this.onlineStatusBool = true;
        } else {
            this.onlineStatusBool = false;
        }
    }


    public String getnewMessageCount() {
        return newMessageCount;
    }
    public void setnewMessageCount(String newMessageCount){
        this.newMessageCount = newMessageCount;
    }
    
}
