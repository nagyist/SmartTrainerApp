package com.smarttrainer.smarttrainer.models;

import java.util.List;

/**
 * Created by fan on 3/12/16.
 */
public interface MotionJudge {
    double Fs = 20;
    JudgeResult judgeMotion(List<float[]> sensorRawData);
    int getCount(List<float[]> sensorRawData);
}