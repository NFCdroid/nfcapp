package com.ag.mk.nfccardreadwrite.tools;

import android.content.Intent;
import android.net.Uri;

import com.ag.mk.nfccardreadwrite.activity.MainActivity;
import com.ag.mk.nfccardreadwrite.addons.Voice;

/**
 * Diese Klasse beinhaltet verschiedene Methoden
 * um die Kontakt Daten direkt an andere Anwendungen im Gerät zu übergeben
 * und zu starten.
 *
 * @author Marko Klepatz
 */
public class ContactTools {

    private int hitCounter = 0;

    private MainActivity mainActivity;

    /**
     *
     * @param mainActivity übergibt die MainActivity für den Context zum ausführen von Code der nur auf dieser ausgeführt werden kann
     *
     * @see MainActivity
     */
    public ContactTools(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    /**
     * Diese Methode generiert einen Intent
     * der die E-Mail Adresse des aktuell ausgewählten Kontakts übergibt
     * und startet eine Auswahl mit allen potenziell einsetzbaren E-Mail Programmen auf dem Gerät.
     *
     * @param email übergibt die E-Mail Adresse des aktuell ausgewählten Kontakts
     */
    public void mailContact(final String email) {
        if(!email.equals("") && !email.equals("---") && !email.equals(" ")) {
            Voice.speakOut("E-Mail Programm wird gerufen.");
            new Thread() {
                public void run() {
                    try {
                        if(Voice.isSound())Thread.sleep(1300);

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "");
                        intent.putExtra(Intent.EXTRA_TEXT, "");
                        mainActivity.startActivity(intent);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        }else{
            Voice.speakOut("Ungültige E-Mail Adresse.");
        }
    }

    public void easterEgg(String name) {

        if(Voice.isSound())hitCounter++;

        if(hitCounter<4) {
            Voice.speakOut(name);
        }else if(hitCounter == 4){
            Voice.speakOut("Haben Sie lange weile?");
        }else if(hitCounter == 5){
            Voice.speakOut("Wie oft denn noch?");
        }else if(hitCounter == 6){
            Voice.speakOut("Nein, ich werde es ihnen nicht noch einmal vorlesen!");
        }else if(hitCounter == 7){
            Voice.speakOut("Sind Sie doof?");
        }else if(hitCounter == 8){
            Voice.speakOut("Hören Sie auf mich zu nerven!");
        }else if(hitCounter == 9){
            Voice.speakOut("Noch einmal und ich rufe die Polizei!");
        }else if(hitCounter == 10) {
            callContact("Polizei", "111"); // Eigentlich 110 aber wir wollen ja keinen Ärger :P
        }else if(hitCounter == 11){
            Voice.speakOut("Haha ich habe Sie nur verarscht!");
        }else if(hitCounter == 12){
            Voice.speakOut("Wenn Sie mich weiter nerven schließe ich diese App!");
        }else if(hitCounter == 13){
            Voice.speakOut("Ok das wars!");
            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.exit(0);
        }
    }

    /**
     * Diese Methode generiert einen Intent
     * welcher die Nummer des Kontaktes übergibt
     * und ruft mit diesem direkt das Programm zum Anrufen von Kontakten auf und ruft diesen an.
     *
     * @param name übergibt den Namen des ausgewählten Kontakts für die Sprachausgabe
     * @param number übergibt die Nummer des ausgewählten Kontakts zum Anrufen
     */
    public void callContact(String name, final String number){

        if(!number.equals("") && !number.equals("---") && !number.equals(" ")) {
            Voice.speakOut(name + " wird angerufen.");
            new Thread() {
                public void run() {
                    try {
                        if(Voice.isSound())Thread.sleep(1300);

                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                        mainActivity.startActivity(intent);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }else{
            Voice.speakOut("Ungültige Rufnummer.");
        }
    }
}
