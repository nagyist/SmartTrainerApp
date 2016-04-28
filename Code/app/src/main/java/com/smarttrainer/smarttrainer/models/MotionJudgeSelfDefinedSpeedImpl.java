package com.smarttrainer.smarttrainer.models;

import java.io.InputStream;
import java.util.List;

/**
 * Created by fan on 4/10/16.
 */
public class MotionJudgeSelfDefinedSpeedImpl extends MotionJudgeImpl{
    private float minFreq;
    private float maxFreq;
    public MotionJudgeSelfDefinedSpeedImpl(){
        super(null);
    }
    public MotionJudgeSelfDefinedSpeedImpl(float minFreq, float maxFreq){
        super(null);
        this.minFreq = minFreq;
        this.maxFreq = maxFreq;
    }

    @Override
    public JudgeResult judgeMotion(List<float[]> sensorRawData) {
        double[] frequencyAndAmpl = getFrequencyAtMaxAmplitude(sensorRawData);

        double maxAmpl = 0;
        double dominatingFreq = 0;
        for (int i = 0; i < 3; i++) {
            if (maxAmpl <= frequencyAndAmpl[i + 3]) {
                maxAmpl = frequencyAndAmpl[i + 3];
                dominatingFreq = frequencyAndAmpl[i];
            }
        }
        if (dominatingFreq < minFreq) {
            return new JudgeResult("Too Slow", 60f);
        } else if (dominatingFreq <= maxFreq) {
            return new JudgeResult("Good", 100f);
        } else {
            return new JudgeResult("Too Fast", 80f);
        }
    }

    public void setMaxFreq(float maxFreq){
        this.maxFreq = maxFreq;
    }

    public void setMinFreq(float minFreq){
        this.minFreq = minFreq;
    }

}
