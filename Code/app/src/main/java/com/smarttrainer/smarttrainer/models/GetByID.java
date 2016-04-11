package com.smarttrainer.smarttrainer.models;

import com.smarttrainer.smarttrainer.R;

/**
 * Created by ld on 4/4/16.
 */
public class GetByID {
    public static String getExerName(int id)
    {
        if (id == 0)
            return "Bench Press";
        else if (id == 1)
            return "Curl";
        else if (id == 2)
            return "Sit-up";
        return null;
    }

    public static int getGIF(int id)
    {
        if (id == 1)
            return R.drawable.curl;
        else if (id == 2)
            return R.drawable.curl;     // TODO: sit-up drawable
        return R.drawable.bench_press_gif;
    }

    public static String getMuscle(int id)
    {
        if (id == 1)
            return "biceps, brachialis";
        return "Gezhong Jirou";
    }

    public static String getInst(int id) {
        if (id == 0)
            return "Lie on your back with your feet flat on the floor. If" +
                    "your feet don’t reach the floor, use a stable board to" +
                    "accommodate size. Grasp the barbell with a wider" +
                    "than shoulder-width grip, wrapping thumbs around" +
                    "the bar. Hold the barbell at arm’s length above your" +
                    "upper-chest area.";
        else if (id == 1)
            return "Use the right arm to pick up the dumbbell and place the back of that upper arm on top of your inner right thigh (around three and a half inches away from the front of the knee). Rotate the palm of the hand until it is facing forward away from your thigh. Your arm should be extended at arms length and the dumbbell should be above the floor.";
        return "Please see the picture.";
    }
}
