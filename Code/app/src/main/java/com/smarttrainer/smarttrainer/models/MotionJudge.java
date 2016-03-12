package com.smarttrainer.smarttrainer.models;

import java.util.ArrayList;

/**
 * Created by fan on 3/12/16.
 */
public interface MotionJudge {
    String judgeMotion(ArrayList<double[]> sensorRawData, int formID);
    void reset();
    int getCount();
}