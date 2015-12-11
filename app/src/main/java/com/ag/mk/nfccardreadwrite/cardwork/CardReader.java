package com.ag.mk.nfccardreadwrite.cardwork;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.util.Log;

import com.ag.mk.nfccardreadwrite.activitys.MainActivity;

import java.io.UnsupportedEncodingException;

/**
 * Created by Marko on 07.11.2015.
 */
public class CardReader {

    private MainActivity mainActivity;

    public CardReader(MainActivity mainActivity){
        this.mainActivity = mainActivity;

    }

    public String readTag(Intent intent){

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

    private String getTextFromNdefRecord(NdefRecord ndefRecord){
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
    }
}
