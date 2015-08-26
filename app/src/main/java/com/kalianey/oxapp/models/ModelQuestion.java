package com.kalianey.oxapp.models;

/**
 * Created by kalianey on 26/08/2015.
 */
public class ModelQuestion {

    private String questionName;
    private String questionValue;
    private String section;

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getQuestionValue() {
        return questionValue;
    }

    public void setQuestionValue(String questionValue) {
        this.questionValue = questionValue;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }


}
