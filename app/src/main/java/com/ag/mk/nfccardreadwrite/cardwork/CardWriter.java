package com.ag.mk.nfccardreadwrite.cardwork;

import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.util.Log;
import android.widget.Toast;

import com.ag.mk.nfccardreadwrite.addons.Voice;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * Diese Klasse beinhaltet die Methoden die für das Beschreiben eines NFC Chips benötigt werden.
 *
 *
 * @author Oliver Friedrich, Marko Klepatz
 */
public class CardWriter {

    private Context context;

    public CardWriter(Context context){
        this.context = context;

    }

    /**
     * Diese Methode formatiert beim Aufruf den Speicher auf dem NFC Chip in das NDEF Format
     * und schreibt danach die Nachricht auf den Chip.
     *
     * @param tag übergibt das Tag-Format
     * @param ndefMessage übergibt die NDEF Nachricht zum beschreiben auf den NFC Chip
     */
    private void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {

            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if (ndefFormatable == null) {
                Toast.makeText(context, "Tag is not ndef formatable!", Toast.LENGTH_SHORT).show();
                return;
            }


            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(context, "Schreibvorgang erfolgreich!", Toast.LENGTH_SHORT).show();
            Voice.speakOut("Schreibvorgang erfolgreich abgeschlossen!");

        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }

    }

    /**
     * Diese Methode schreibt die NDEF Nachricht auf den NFC Chip.
     *
     * @param tag übergibt das Tag-Format
     * @param ndefMessage übergibt die NDEF Nachricht zum beschreiben auf den NFC Chip
     */
    public void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {

        try {

            if (tag == null) {
                Toast.makeText(context, "Tag object cannot be null", Toast.LENGTH_SHORT).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);

            if (ndef == null) {
                // format tag with the ndef format and writes the message.
                formatTag(tag, ndefMessage);
            } else {
                ndef.connect();

                if (!ndef.isWritable()) {
                    Toast.makeText(context, "Tag is not writable!", Toast.LENGTH_SHORT).show();

                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();

                Toast.makeText(context, "Schreibvorgang erfolgreich!", Toast.LENGTH_SHORT).show();
                Voice.speakOut("Schreibvorgang erfolgreich abgeschlossen!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Diese Methode generiert die NDEF Message mit dem speziellen Mime Type für diese App.
     *
     * @param content übergibt den Inhalt der auf die Karte geschrieben werden soll
     * @return gibt die generierte NDEF Message zurück
     */
    public NdefMessage createNdefMessage(String content) {
        return new NdefMessage(
                new NdefRecord[]{
                        NdefRecord.createMime(
                                "application/vnd.com.ag.mk.nfccardreadwrite.beam",
                                content.getBytes())
                });
    }

    /**
     * Hier folgt noch eine Methode die, die Daten in ein universelleres Format bringt allerings nicht so elegant.
     */

    /*
    private NdefRecord transformToHexCode(String content) {
        try {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());

        } catch (UnsupportedEncodingException e) {
            Log.e("transformToHexCode", e.getMessage());
        }
        return null;
    }
    */
}
