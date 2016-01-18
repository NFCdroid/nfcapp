package com.ag.mk.nfccardreadwrite.addons;

import android.os.Vibrator;

/**
 * Diese Klasse ist für die Vibrationsfunktion, insofern aktiviert,
 * für alle Klassen und Activities zuständig. <br><br>
 * Sie muss einmal über ihren Konstruktor initialisiert werden
 * und ist dann für alle Klassen, durch ihren
 * statischen Charakter verfügbar.<br><br>
 * Vibrator ist die zu initialisierende Klasse mit der die Vibrationsfunktion ausgeführt werden kann. <br><br>
 *
 * @author Marko Klepatz
 */
public class Vibration {

    private static Vibrator vibrator;

    private static boolean vibration = false;

    /**
     * Dieser Konstruktor nimmt die in der MainActivity initialisierte Vibrator Klasse entgegen,
     * die hier benötigt wird um die Vibration auszuführen.
     *
     * @param vibrator übergibt die benötigte Vibrator Klasse zum Ausführen der Vibration
     */
    public Vibration(Vibrator vibrator){
        Vibration.vibrator = vibrator;
    }

    public static void setVibration(boolean vibration){
        Vibration.vibration = vibration;
    }

    public static boolean isVibration() {
        return vibration;
    }

    /**
     * Diese Methode ist beim Aufruf dafür zuständig, dass das Gerät 25 Millisekunden vibriert.<br><br>
     * Zusätzlich wird geprüft ob die Variable <b>vibration</b> auf true oder false gestellt ist.<br><br>
     *
     * true: vibriert<br>
     * false: vibriert nicht<br>
     */
    public static void vibrate(){

        if(vibration){
            vibrator.vibrate(25);
        }
    }
}
