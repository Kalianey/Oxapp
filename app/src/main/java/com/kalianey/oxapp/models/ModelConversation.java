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
    @SerializedName("displayName")
    private String name;
    private String avatarUrl;
    private String previewText;
    private String timeLabel;
    private String conversationViewed;
    private String conversationRead;
    private String conversationReplied;
    private String onlineStatusBool;
    private String onlineStatusString;
    private String newMessageCount;

    //private List<ModelConversation> conv ; //initialized with a null

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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

    public String getonlineStatusBool() {
        return onlineStatusBool;
    }
    public void setonlineStatusBool(String onlineStatusBool){
        this.onlineStatusBool = onlineStatusBool;
    }

    public String getonlineStatusString() {
        return onlineStatusString;
    }
    public void setonlineStatusString(String onlineStatusString){
        this.onlineStatusString = onlineStatusString;
    }

    public String getnewMessageCount() {
        return newMessageCount;
    }
    public void setnewMessageCount(String newMessageCount){
        this.newMessageCount = newMessageCount;
    }
    
}
