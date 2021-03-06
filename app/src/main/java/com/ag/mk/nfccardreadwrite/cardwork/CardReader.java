package com.ag.mk.nfccardreadwrite.cardwork;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.util.Log;

import com.ag.mk.nfccardreadwrite.addons.Voice;

/**
 * Diese Klasse beinhaltet die Methode zum Auslesen der Kartendaten. <br><br>
 *
 * @author Oliver Friedrich,  Marko Klepatz
 */
public class CardReader {

    /**
     * Diese Methode liest aus dem übergebenem Intent die Daten aus,
     * die sich auf der Karte befinden und wandelt sie in einen String um.<br><br>
     *
     * @param intent enthält die Daten die auf der Karte sind
     * @return gibt den String mit den Daten auf der Karte zurück
     */
    public static String readTag(Intent intent) {

        try {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            // only one message sent during the beam
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            // record 0 contains the MIME type, record 1 is the AAR, if present
            return new String(msg.getRecords()[0].getPayload());

        }catch (Exception e){
            Log.e("ERROR", "Read error in CardReader Class");
            Voice.speakOut("Lesefehler, bitte versuchen Sie es erneut!");
        }

        return  null;
    }


    /**
     * Hier folgen noch 2 Methoden die etwas individueller die Daten auslesen allerdings sind sie nicht so elegant.
     */

   /* public static String readTag(Intent intent){

        String tag = null;

        Parcelable[] parcelable = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        NdefMessage ndefMessage = null;

        if(parcelable != null && parcelable.length > 0){

            ndefMessage = (NdefMessage)parcelable[0];

            NdefRecord[] ndefRecords = ndefMessage.getRecords();

            if(ndefRecords != null && ndefRecords.length > 0){

                NdefRecord ndefRecord = ndefRecords[0];

                tag = getTextFromNdefRecord(ndefRecord);
            }

        }
        return tag;
    }

   /* private static String getTextFromNdefRecord(NdefRecord ndefRecord){
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }*/
}
