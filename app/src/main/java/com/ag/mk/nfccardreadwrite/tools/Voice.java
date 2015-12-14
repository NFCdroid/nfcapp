package com.ag.mk.nfccardreadwrite.tools;

import android.speech.tts.TextToSpeech;

/**
 * Created by Marko on 18.09.2015.
 */
public class Voice {

    private TextToSpeech textToSpeech;

    private boolean sound = false;

    public Voice(TextToSpeech textToSpeech){
        this.textToSpeech = textToSpeech;

    }

    public void speakOut(String message) {
        if(sound == true) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public boolean isSound(){
        return sound;
    }

    public void setSound(boolean sound){
        this.sound = sound;
    }

    public void voiceOn(){
        speakOut("Sprachausgabe eingeschaltet.");
    }

    public void voiceOff(){
        speakOut("Sprachausgabe abgeschaltet.");
    }

    public void vibrationOn(){
        speakOut("Vibration eingeschaltet.");
    }

    public void vibrationOff(){
        speakOut("Vibration abgeschaltet.");
    }

}
