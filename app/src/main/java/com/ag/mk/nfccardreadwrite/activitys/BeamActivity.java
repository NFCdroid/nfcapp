/*This example was taken from
https://developer.android.com/guide/topics/connectivity/nfc/nfc.html#p2p
 */

package com.ag.mk.nfccardreadwrite.activitys;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.ag.mk.nfccardreadwrite.R;
import com.ag.mk.nfccardreadwrite.cardwork.CardWriter;
import com.ag.mk.nfccardreadwrite.tools.VCardFormatTool;

/**
 * Diese Activity erlaubt eine P2P Verbindung mit anderen NFC-fähigen Handys.
 * Statt den Lowlevel NFC-Funktionen wird hier Android BeamActivity zum Austausch von NDEF-Nachrichten genutzt.
 * Das Intent-Handling entfällt wenn BeamActivity im Vordergrund läuft.
 */

public class BeamActivity extends Activity implements CreateNdefMessageCallback {
    private NfcAdapter mNfcAdapter;
    private TextView textView;

    private String vCardInformation = null;

    private CardWriter cardWriter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beam);
        TextView textView = (TextView) findViewById(R.id.textView);
        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        cardWriter = new CardWriter(this);

        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setVCardProfile(); //TODO zu Testzwecken kommt noch weg!
        // Register callback
        mNfcAdapter.setNdefPushMessageCallback(this, this);
    }


    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        //String text = ();
        NdefMessage msg = cardWriter.createNdefMessage(vCardInformation);

                /*new NdefMessage(
                new NdefRecord[] { createMime(
                        "application/vnd.com.ag.mk.nfccardreadwrite.beam", text.getBytes())
                        /*
                         * The Android Application Record (AAR) is commented out. When a device
                         * receives a push with an AAR in it, the application specified in the AAR
                         * is guaranteed to run. The AAR overrides the tag dispatch system.
                         * You can add it back in to guarantee that this
                         * activity starts when receiving a beamed message. For now, this code
                         * uses the tag dispatch system..

                        //,NdefRecord.createApplicationRecord("package com.ag.mk.nfccardreadwrite.MainActivity")
                });*/
        return msg;
    }

    public void setVCardProfile(){
        vCardInformation =VCardFormatTool.getFormatedVCardString("Hans","0815","12345","hans@wurst.de");
    }
}