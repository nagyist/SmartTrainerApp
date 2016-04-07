package com.smarttrainer.smarttrainer.models;

/**
 * Created by ld on 4/4/16.
 */
public class GetByID {
    public static String getExerName(int id)
    {
        if (id == 0)
            return "Bench Press";
        else if (id == 1)
            return "Curl";
        return null;
    }
}
