package com.ag.mk.nfccardreadwrite.addons;

import android.speech.tts.TextToSpeech;

/**
 * Created by Marko on 18.09.2015.
 */
public class Voice {

    private static TextToSpeech textToSpeech;

    private static boolean sound = false;

    public Voice(TextToSpeech textToSpeech){
        this.textToSpeech = textToSpeech;

    }

    public static void speakOut(String message) {
        if(sound) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public static boolean isSound(){
        return sound;
    }

    public static void setSound(boolean sound){
        Voice.sound = sound;

        if (!sound){
            textToSpeech.speak("Sprachausgabe deaktiviert.", TextToSpeech.QUEUE_FLUSH, null);
        }else {
            textToSpeech.speak("Sprachausgabe aktiviert.", TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
