package com.smarttrainer.smarttrainer.models;

import java.util.List;

/**
 * Created by fan on 3/12/16.
 */
public interface MotionJudge {
    double Fs = 20;
    String judgeMotion(List<float[]> sensorRawData);
    void reset();
    int getCount(List<float[]> sensorRawData);
}