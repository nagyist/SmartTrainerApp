package com.smarttrainer.smarttrainer.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fan on 3/12/16.
 */
public interface MotionJudge {
    String judgeMotion(List<double[]> sensorRawData, int formID);
    void reset();
    int getCount();
}