package com.smarttrainer.smarttrainer.models;

import org.jtransforms.fft.FloatFFT_1D;

import weka.classifiers.Classifier;
import weka.core.FastVector;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fan on 4/6/16.
 */
public class MotionJudgeImpl implements MotionJudge{
    private List<float[]> allSensorRawData;
    private FastVector attributes;
    private Classifier myClassifier;

    public MotionJudgeImpl(InputStream model){
        allSensorRawData = new ArrayList<>();
        attributes = buildAttribute();
        try {
            myClassifier = (Classifier) weka.core.SerializationHelper.read(model);
        } catch (Exception e){}
    }

    public String judgeMotion(List<float[]> sensorRawData){
        this.allSensorRawData.addAll(sensorRawData);
        Instance instance = new Instance(7);
        for (int k = 0; k < 6; k++) {
            instance.setValue((Attribute) attributes.elementAt(k),
                    this.getFrequencyAtMaxAmplitude(sensorRawData)[k]);
        }

        Instances training = new Instances("test", attributes, 1);
        training.setClassIndex(6);
        training.add(instance);
        try {
            int motionClass = (int)(Math.round(myClassifier.classifyInstance(training.instance(0))));
            if(motionClass==0){
                return "Too Slow";
            } else if(motionClass==1){
                return "Good";
            } else if(motionClass==2){
                return "Too Fast";
            } else if(motionClass==3){
                return "Out of direction";
            } else{
                return "Error";
            }
        } catch (Exception e){
            return "Error";
        }
    }


    public int getCount() {
        int L = this.allSensorRawData.size();
        if(L==0){
            return 0;
        }
        double T = L*(1/Fs);
        double frequencyAtMaxAmp = this.getFrequencyAtMaxAmplitude(this.allSensorRawData)[0];
        return (int)(T*frequencyAtMaxAmp);
    }


    public void reset() {
        this.allSensorRawData.clear();
    }


    private double[] getFrequencyAtMaxAmplitude(List<float[]> sensorRawData){
        double[] feature = new double[6];

        for (int j = 0; j < 3; j++) {
            int L = sensorRawData.size();
            FloatFFT_1D fft = new FloatFFT_1D(L);
            float T = (float) (L * (1 / Fs));
            float[] dataInYAxis = new float[L * 2];
            for (int i = 0; i < L; i++) {
                dataInYAxis[i] = sensorRawData.get(i)[j];
            }
            fft.realForwardFull(dataInYAxis);
            double[] P2 = new double[L];
            for (int i = 0; i < L; i++) {
                P2[i] = Math.abs(dataInYAxis[i] / L);
            }
            double[] P1 = new double[L];
            System.arraycopy(P2, 0, P1, 0, L / 2);
            for (int i = 1; i < L - 1; i++) {
                P1[i] = 2 * P1[i];
            }
            int offset = 2;
            double maxAmplitude = 0;
            double frequencyAtMaxAmp = 0;
            for (int i = offset; i < L / 2; i++) {
                if (maxAmplitude < P1[i]) {
                    maxAmplitude = P1[i];
                    frequencyAtMaxAmp = Fs * i / L / 2;
                }
            }
            feature[j] = frequencyAtMaxAmp;
            feature[j+3] = maxAmplitude;
        }
        return feature;
    }

    private FastVector buildAttribute(){
        FastVector fvWekaAttributes = new FastVector(7);
        for(int i=0;i<7;i++){
            fvWekaAttributes.addElement(new Attribute(String.valueOf(i),i));
        }
        return fvWekaAttributes;
    }
}
