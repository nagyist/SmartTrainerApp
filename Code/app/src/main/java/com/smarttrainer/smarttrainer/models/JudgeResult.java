package com.smarttrainer.smarttrainer.models;

/**
 * Created by fan on 4/10/16.
 */
public class JudgeResult {
    private String description;
    private float score;
    public JudgeResult(){
        this("",0f);
    }

    public JudgeResult(String description, float score){
        this.description = description;
        this.score = score;
    }

    public String getDescription(){
        return this.description;
    }

    public float getScore(){
        return this.score;
    }
}
