package com.ag.mk.nfccardreadwrite.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Diese Klasse beinhaltet die Methoden,
 * die für die V-Card Formatierung
 * und das Extrahieren der Informationen aus dem V-Card Format benötigt werden.
 *
 * @author Marko Klepatz, Klaus Steinhauer
 */
public class VCardFormatTools {

    private static final String NAME = "N:";
    private static final String MOBILENUMBER = "TEL;TYPE=WORK,VOICE:";
    private static final String HOMENUMBER = "TEL;TYPE=HOME,VOICE:";
    private static final String EMAIL = "EMAIL;TYPE=PREF,INTERNET:";

    /**
     * Diese Methode extrahiert die Informationen aus einem V-Card String
     * und bringt sie in ein für diese App sinnvolles Ausgabeformat.
     *
     * @param cardContent übergibt die rohen Karten-Daten
     * @return vCardInformationList gibt eine Liste mit allen extrahierten Daten zurück
     */
    public static ArrayList<String> extractCardInformation(String[] cardContent){

        ArrayList<String> vCardInformationList = new ArrayList<String>();

        for(int i=0; i<cardContent.length; i++){
            if(cardContent[i].startsWith(NAME)){

                vCardInformationList.add(0, "Name: " + checkResult(cardContent[i], NAME).replace(";", " "));

            }else if(cardContent[i].startsWith(MOBILENUMBER)){

                vCardInformationList.add(1, "Telefon-Mobil: " + checkResult(cardContent[i], MOBILENUMBER));

            }else if(cardContent[i].startsWith(HOMENUMBER)){

                vCardInformationList.add(2, "Telefon-Festnetz: " + checkResult(cardContent[i], HOMENUMBER));

            }else if(cardContent[i].startsWith(EMAIL)){

                vCardInformationList.add(3, "E-Mail: " + checkResult(cardContent[i], EMAIL));
            }
        }

        return  vCardInformationList;
    }

    /**
     * Diese Methode wird gerufen um eine ArrayIndexOutOfBounds Exception bei leeren Feldern zu vermeiden.
     * Ist das Feld leer so wird ein String mit dem Inhalt "" zurück gegeben.
     *
     * @param result übergibt die rohen V-Card Daten zum Überprüfen
     * @param checkParameter übergibt den Prüfparameter zum Überprüfen der V-Card Rohdaten
     * @return gibt das Resultat zurück
     */
    private static String checkResult(String result, String checkParameter){
        if (result.split(checkParameter).length > 1) {
            return result.split(checkParameter)[1];
        }
        else {
            return "";
        }
    }

    /**
     * Diese Methode formatiert die Roh-Adress-Daten in einen V-Card-Format String.
     *
     * @param userName übergibt den Namen des gewählten Kontaktes
     * @param mobileNumber übergibt die Mobilnummer des gewählten Kontaktes
     * @param homeNumber übergibt die Festnetznummer des gewählten Kontaktes
     * @param eMail übergibt die E-Mail Adresse des gewählten Kontaktes
     * @return gibt den V-Card-Format String zurück
     */
    public static String getFormatedVCardString(String userName, String mobileNumber, String homeNumber, String eMail){

        return "BEGIN:vcard\r\n"
                + "VERSION:3.0\r\n"
                + "N:" + userName.replace(" ",";") + "\r\n"
                + "TEL;TYPE=WORK,VOICE:" + mobileNumber + "\r\n"
                + "TEL;TYPE=HOME,VOICE:" + homeNumber + "\r\n"
                + "EMAIL;TYPE=PREF,INTERNET:" + eMail + "\r\n"
                + "REV:" + VCardFormatTools.getTimeStamp() + "\r\n"
                + "END:vcard\r\n";
    }

    /**
     * Diese Methode generiert einen Zeitstempel für die V-Card und gibt diesen zurück.
     *
     * @return gibt den Zeitstempel für die V-Card zurück
     */
    private static String getTimeStamp(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return (dateFormat.format(new Date()) + "Z").replace(" ", "T");// Beispielausgabe: REV:2014-03-01T22:11:10Z
    }
}
