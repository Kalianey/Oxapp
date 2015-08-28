package com.kalianey.oxapp.models;

import java.util.ArrayList;

/**
 * Created by kalianey on 28/08/2015.
 */
public class ModelFavorite {

    String myMenuLabel;
    String meMenuLabel;
    String mutualMenuLabel;
    ArrayList<ModelUser> my;
    ArrayList<ModelUser> me;
    ArrayList<ModelUser> mutual;

    public String getMyMenuLabel() {
        return myMenuLabel;
    }

    public void setMyMenuLabel(String myMenuLabel) {
        this.myMenuLabel = myMenuLabel;
    }

    public String getMeMenuLabel() {
        return meMenuLabel;
    }

    public void setMeMenuLabel(String meMenuLabel) {
        this.meMenuLabel = meMenuLabel;
    }

    public String getMutualMenuLabel() {
        return mutualMenuLabel;
    }

    public void setMutualMenuLabel(String mutualMenuLabel) {
        this.mutualMenuLabel = mutualMenuLabel;
    }

    public ArrayList<ModelUser> getMy() {
        return my;
    }

    public void setMy(ArrayList<ModelUser> my) {
        this.my = my;
    }

    public ArrayList<ModelUser> getMe() {
        return me;
    }

    public void setMe(ArrayList<ModelUser> me) {
        this.me = me;
    }

    public ArrayList<ModelUser> getMutual() {
        return mutual;
    }

    public void setMutual(ArrayList<ModelUser> mutual) {
        this.mutual = mutual;
    }


}
