package com.ag.mk.nfccardreadwrite.addons;

import android.os.Vibrator;

/**
 * Created by Marko on 25.09.2015.
 */
public class Vibration {

    private static Vibrator vibrator;

    private static boolean vibration = false;

    public Vibration(Vibrator vibrator){
        this.vibrator = vibrator;
    }

    public static void setVibration(boolean vibration){
        Vibration.vibration = vibration;
    }

    public static boolean isVibration() {
        return vibration;
    }

    public static void vibrate(){

        if(vibration){
            vibrator.vibrate(25);
        }
    }
}
