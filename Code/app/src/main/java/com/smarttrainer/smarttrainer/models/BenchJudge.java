package com.smarttrainer.smarttrainer.models;

import android.util.Log;
import org.jtransforms.fft.FloatFFT_1D;

import java.util.List;
import java.util.ArrayList;
;


/**
 * Created by fan on 3/12/16.
 */
public class BenchJudge implements MotionJudge{
    List<float[]> allSensorRawData;
    int count;
    double Fs;

    public BenchJudge(){
        allSensorRawData = new ArrayList<float[]>();
        count = 0;
        this.Fs = 20;
    }

    private double getFrequencyAtMaxAmplitude(List<float[]> sensorRawData){
        int L = sensorRawData.size();
        FloatFFT_1D fft = new FloatFFT_1D(L);
        float T = (float)(L*(1/this.Fs));
        float[] dataInYAxis = new float[L*2];
        for(int i=0;i<L;i++){
            dataInYAxis[i] = sensorRawData.get(i)[0];
        }
        fft.realForwardFull(dataInYAxis);
        double[] P2 = new double[L];
        for(int i=0;i<L;i++){
            P2[i] = Math.abs(dataInYAxis[i]/L);
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
            if(maxAmplitude<P1[i] && P1[i]>0.1){
                maxAmplitude = P1[i];
                frequencyAtMaxAmp = Fs*i/L/2;
            }
        }
        return frequencyAtMaxAmp;
    }


    public String judgeMotion(List<float[]> sensorRawData, int formID) {
        this.allSensorRawData.addAll(sensorRawData);

        if(formID == 0){
            double frequencyAtMaxAmp = this.getFrequencyAtMaxAmplitude(sensorRawData);
            if(frequencyAtMaxAmp<0.2){
                return "Too Slow.";
            } else if(frequencyAtMaxAmp<=0.7){
                return "Good.";
            } else{
                return "Too Fast";
            }
        }
        return "Cannot judge this form.";
    }

    public int getCount() {
        int L = this.allSensorRawData.size();
        if(L==0){
            return 0;
        }
        double T = L*(1/this.Fs);
        double frequencyAtMaxAmp = this.getFrequencyAtMaxAmplitude(this.allSensorRawData);
        return (int)(T*frequencyAtMaxAmp);
    }


    public void reset() {
        this.allSensorRawData.clear();
    }
}
