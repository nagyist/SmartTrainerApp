package com.smarttrainer.smarttrainer.models;

import android.util.Log;

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
    List<double[]> allSensorRawData;
    int count;
    FastFourierTransformer fft;
    double Fs;

    public BenchJudge(){
        allSensorRawData = new ArrayList<>();
        count = 0;
        this.Fs = 20;
        this.fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Log.d("Constructor"," ");
    }

    private double getFrequencyAtMaxAmplitude(List<double[]> sensorRawData){
        int L = sensorRawData.size();
        double T = L*(1/this.Fs);
        double[] dataInYAxis = new double[1<<8];
        for(int i=0;i<L-10;i++){
            dataInYAxis[i] = sensorRawData.get(i+10)[0];
            Log.d("xzy",""+sensorRawData.get(i+10)[0]);
        }
        Complex[] Y = this.fft.transform(dataInYAxis, TransformType.FORWARD);
        double[] P2 = new double[L+1];
        for(int i=0;i<L;i++){
            P2[i] = Math.abs(Y[i].getReal()/L);
        }
        double[] P1 = new double[L+1];
        System.arraycopy(P2,0,P1,0,L/2);
        for(int i=1;i<L-1;i++){
            P1[i] = 2*P1[i];
        }
        int offset = 5;
        double maxAmplitude = 0;
        double frequencyAtMaxAmp = 0;
        for(int i=offset;i<L/2;i++){
            if(maxAmplitude<P1[i]){
                maxAmplitude = P1[i];
                frequencyAtMaxAmp = Fs*i/L;
            }
        }
        return frequencyAtMaxAmp;
    }

    @Override
    public String judgeMotion(List<double[]> sensorRawData, int formID) {
        this.allSensorRawData.addAll(sensorRawData);

        if(formID == 0){
            double frequencyAtMaxAmp = this.getFrequencyAtMaxAmplitude(sensorRawData);
            Log.d("frequency", "" + frequencyAtMaxAmp);
            if(frequencyAtMaxAmp<1.1){
                return "Too Slow.";
            } else if(frequencyAtMaxAmp<=2){
                return "Good.";
            } else{
                return "Too Fast";
            }
        }
        return "Cannot judge this form.";
    }

    @Override
    public int getCount() {
        int L = this.allSensorRawData.size();
        double T = L*(1/this.Fs);
        double frequencyAtMaxAmp = this.getFrequencyAtMaxAmplitude(this.allSensorRawData);
        return (int)(T*frequencyAtMaxAmp);
    }

    @Override
    public void reset() {
        this.allSensorRawData.clear();
    }
}
