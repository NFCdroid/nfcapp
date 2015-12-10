package com.ag.mk.nfccardreadwrite.cardwork;

import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.util.Log;
import android.widget.Toast;

import com.ag.mk.nfccardreadwrite.activity.CreateVCardActivity;
import com.ag.mk.nfccardreadwrite.tools.VCardFormatTool;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * Created by Marko on 07.11.2015.
 */
public class CardWriter {

    private Context context;

    public CardWriter(Context context){
        this.context = context;

    }

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

            Toast.makeText(context, "Tag written!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }

    }

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

                Toast.makeText(context, "Tag written!", Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {
           e.printStackTrace();
        }

    }


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

    /**
     * Message wird gebaut!
     * @param content
     * @return
     */
    public NdefMessage createNdefMessage(String content) {

        /**
         * ohli:
         * Hier könnte man statt der Hexkonvertierung einfach einen Mime-Type schreiben.
         * Praktisch wäre z.B. text/vcard (ab vCard v4)
         */


        NdefMessage ndefMessage = new NdefMessage(
                new NdefRecord[]{NdefRecord.createMime(
                        "application/vnd.com.ag.mk.nfccardreadwrite.beam",
                        content.getBytes())
                });

/*
        NdefRecord ndefRecord = transformToHexCode(content);

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});
*/
        return ndefMessage;
    }
}
