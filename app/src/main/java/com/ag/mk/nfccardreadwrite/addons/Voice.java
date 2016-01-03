package com.ag.mk.nfccardreadwrite.addons;

import android.speech.tts.TextToSpeech;

/**
 * Diese Klasse ist für die Sprachausgabe, in sofern aktiviert,
 * für alle Klassen und Activities zuständig. <br><br>
 *
 * Sie muss einmal über ihren Konstruktor initialisiert werden
 * und ist dann für alle Klassen, durch ihren
 * statischen Charackter verfügbar.<br><br>
 *
 * Vibrator ist die zu initialisierende Klasse mit der die Vibrationsfunktion ausgeführt werden kann. <br><br>
 *
 * @author Marko Klepatz
 */
public class Voice {

    private static TextToSpeech textToSpeech;

    private static boolean sound = false;

    public Voice(TextToSpeech textToSpeech){
        Voice.textToSpeech = textToSpeech;

    }

    /**
     * Diese Methode ist beim aufruf für die Sprachausgabe zuständig.<br><br>
     *
     * Zusätzlich wird geprüft ob die Variable <b>sound</b> auf true oder false gestellt ist.<br><br>
     *
     * true: Sprachausgabe wird ausgeführt<br>
     * false: Sprachausgabe wird nicht ausgeführt<br>
     *
     * @param message übergibt die Nachricht, welche über die Sprachausgabe ausgegeben wird
     */
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
