/*This example was taken from
https://developer.android.com/guide/topics/connectivity/nfc/nfc.html#p2p
 */

package com.ag.mk.nfccardreadwrite.activity;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;
import android.widget.Toast;

import com.ag.mk.nfccardreadwrite.R;
import com.ag.mk.nfccardreadwrite.cardwork.CardWriter;

import static android.nfc.NdefRecord.createMime;

/**
 * Diese Activity erlaubt eine P2P Verbindung mit anderen NFC-fähigen Handys.
 * Statt den Lowlevel NFC-Funktionen wird hier Android Beam zum Austausch von NDEF-Nachrichten genutzt.
 * Das Intent-Handling entfällt wenn Beam im Vordergrund läuft.
 */

public class Beam extends Activity implements CreateNdefMessageCallback {
    private NfcAdapter mNfcAdapter;
    private TextView textView;

    private CardWriter emuWriterCardWriter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beam);
        TextView textView = (TextView) findViewById(R.id.textView);
        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        emuWriterCardWriter = new CardWriter(null);

        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Register callback
        mNfcAdapter.setNdefPushMessageCallback(this, this);
    }


    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        //String text = ();
        NdefMessage msg = emuWriterCardWriter.createNdefMessage("Hans","0815","12345","hans@wurst.de");

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

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        textView = (TextView) findViewById(R.id.textView);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        textView.setText(new String(msg.getRecords()[0].getPayload()));
    }
}