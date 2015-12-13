package com.ag.mk.nfccardreadwrite.tools;

import android.os.Vibrator;

/**
 * Created by Marko on 25.09.2015.
 */
public class Vibration {

    private static Vibrator vibrator;

    private static boolean vibration;

    public Vibration(Vibrator vibrator){
        this.vibrator = vibrator;
    }

    public void setVibration(boolean vibration){
        this.vibration = vibration;
    }

    public static boolean isVibration() {
        return vibration;
    }

    public static void vibrate(){

        if(vibration == true){
            vibrator.vibrate(25);
        }
    }
}
