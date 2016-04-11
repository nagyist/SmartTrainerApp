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
    private FastVector attributes;
    private Classifier myClassifier;

    public MotionJudgeImpl(InputStream model){
        attributes = buildAttribute();
        try {
            myClassifier = (Classifier) weka.core.SerializationHelper.read(model);
        } catch (Exception e){}
    }

    public JudgeResult judgeMotion(List<float[]> sensorRawData){
        Instance instance = new Instance(7);
        double[] frequencyAndAmpl = this.getFrequencyAtMaxAmplitude(sensorRawData);
        for (int k = 0; k < 6; k++) {
            instance.setValue((Attribute) attributes.elementAt(k),
                    frequencyAndAmpl[k]);
        }

        Instances training = new Instances("test", attributes, 1);
        training.setClassIndex(6);
        training.add(instance);
        try {
            int motionClass = (int)(Math.round(myClassifier.classifyInstance(training.instance(0))));
            if(motionClass==0){
                return new JudgeResult("Too Slow", 60.0f);
            } else if(motionClass==1){
                return new JudgeResult("Good", 100.0f);
            } else if(motionClass==2){
                return new JudgeResult("Too Fast", 80);
            } else if(motionClass==3){
                return new JudgeResult("Wrong , your palm should be facing up.", 50);
            } else{
                return new JudgeResult("Error", 0f);
            }
        } catch (Exception e){
            return new JudgeResult("Error", 0f);
        }
    }



    public int getCount(List<float[]> sensorRawData) {
        int L = sensorRawData.size();
        if(L==0){
            return 0;
        }
        double T = L*(1/Fs);
        double frequencyAtMaxAmp = this.getFrequencyAtMaxAmplitude(sensorRawData)[0];
        return (int)(T*frequencyAtMaxAmp);
    }


    protected double[] getFrequencyAtMaxAmplitude(List<float[]> sensorRawData){
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
        Attribute Attribute1 = new Attribute("firstNumeric",0);
        Attribute Attribute2 = new Attribute("secondNumeric",1);
        Attribute Attribute3 = new Attribute("thirdNumeric",2);
        Attribute Attribute4 = new Attribute("fourthNumeric",3);
        Attribute Attribute5 = new Attribute("fifthNumeric",4);
        Attribute Attribute6 = new Attribute("sixthNumeric",5);
        FastVector fvClassVal = new FastVector(4);
        fvClassVal.addElement("slow");
        fvClassVal.addElement("standard");
        fvClassVal.addElement("fast");
        fvClassVal.addElement("out_of_direction");
        Attribute Attribute7 = new Attribute("theClass",fvClassVal,6);
        fvWekaAttributes.addElement(Attribute1);
        fvWekaAttributes.addElement(Attribute2);
        fvWekaAttributes.addElement(Attribute3);
        fvWekaAttributes.addElement(Attribute4);
        fvWekaAttributes.addElement(Attribute5);
        fvWekaAttributes.addElement(Attribute6);
        fvWekaAttributes.addElement(Attribute7);
        return fvWekaAttributes;
    }
}
