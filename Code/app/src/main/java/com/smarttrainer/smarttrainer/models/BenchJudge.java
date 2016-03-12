package com.smarttrainer.smarttrainer.models;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.TransformType;

import java.util.List;
import java.util.ArrayList;
;


/**
 * Created by fan on 3/12/16.
 */
public class BenchJudge implements MotionJudge{
    List<double[]> sensorRawData;
    int count;
    FastFourierTransformer fft;
    double Fs;

    public BenchJudge(){
        sensorRawData = new ArrayList<>();
        count = 0;
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);

    }

    private double getFrequencyAtMaxAmplitude(List<double[]> sensorRawData){
        int L = sensorRawData.size();
        double T = L*(1/this.Fs);
        double[] dataInYAxis = new double[L];
        for(int i=0;i<L;i++){
            dataInYAxis[i] = sensorRawData.get(i)[1];
        }
        Complex[] Y = fft.transform(dataInYAxis, TransformType.FORWARD);
        double[] P2 = new double[L];
        for(int i=0;i<L;i++){
            P2[i] = Math.abs(Y[i].getReal());
        }
        double[] P1 = new double[L];
        System.arraycopy(P2,0,P1,0,L/2);
        for(int i=1;i<L-1;i++){
            P1[i] = 2*P1[i];
        }
        int offset = 2;
        double maxAmplitude = 0;
        double frequencyAtMaxAmp = 0;
        for(int i=offset;i<L/2;i++){
            if(maxAmplitude<P1[i]){
                maxAmplitude = P1[i];
                frequencyAtMaxAmp = T*(Fs*i/L);
            }
        }
        return frequencyAtMaxAmp;
    }

    @Override
    public String judgeMotion(List<double[]> sensorRawData, int formID) {
        this.sensorRawData.addAll(sensorRawData);

        if(formID == 0){
            double frequencyAtMaxAmp = this.getFrequencyAtMaxAmplitude(sensorRawData);
            if(frequencyAtMaxAmp<0.3){
                return "Too Slow.";
            } else if(frequencyAtMaxAmp<=0.4){
                return "Good.";
            } else{
                return "Too Fast";
            }
        }
        return "Cannot judge this form.";
    }

    @Override
    public int getCount() {
        int L = this.sensorRawData.size();
        double T = L*(1/this.Fs);
        double frequencyAtMaxAmp = this.getFrequencyAtMaxAmplitude(this.sensorRawData);
        return (int)(T*frequencyAtMaxAmp);
    }

    @Override
    public void reset() {
        this.sensorRawData.clear();
    }
}
