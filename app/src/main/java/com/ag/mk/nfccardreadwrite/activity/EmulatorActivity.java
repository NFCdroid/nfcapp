package com.ag.mk.nfccardreadwrite.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.ag.mk.nfccardreadwrite.R;

/**
 * Diese Activity erlaubt eine P2P Verbindung mit anderen NFC-fähigen Handys.
 * Statt den Lowlevel NFC-Funktionen wird hier Android Beam zum Austausch von NDEF-Nachrichten genutzt.
 * Das Intent-Handling entfällt wenn Beam im Vordergrund läuft.
 */

public class EmulatorActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {
    public static final String TAG = "TagTest";
    private NfcAdapter eNfcAdapter;

    private TextView emulationTextView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emulator);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        initTextViews();

        //Check for NFC adapter
        eNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (eNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //Register Callback
        eNfcAdapter.setNdefPushMessageCallback(this, this);

    }

    /**
     * Hier bauen wir die NdefMessage zusammen.
     * Genutzt wird unser eigener Mimetype mit einem einfachen Text.
     **/
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String emuText = ("Wenn du das lesen kannst, hat's geklappt!\n\n" +
                "Beam Time: " + System.currentTimeMillis());

        NdefMessage msg = new NdefMessage(
                new NdefRecord[]{NdefRecord.createMime(
                        "application/vnd.com.ag.mk.nfccardreadwrite", emuText.getBytes()),

                        /**
                         * Hier könnten wir auch einen Android Application Record (AAR) mit unserem Appnamen
                         * erstellen. Dadurch sucht das empfangende System nach der App und lädt sie bei
                         * Bedarf automatisch aus dem Play Store.
                         * das sähe dann so aus:
                         **/
                        NdefRecord.createApplicationRecord("com.ag.mk.nfccardreadwrite")
                });
        return msg;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Überprüfe ob die Activity durch Android Beam gestartet wurde
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    //TODO: ggf. aus cardwork.CardReader.java importieren
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);

        // Es kann immer nur eine Nachricht gesendet werden
        NdefMessage msg = (NdefMessage) rawMsgs[0];

        emulationTextView.setText(new String(msg.getRecords()[0].getPayload()));

    }

    private void initTextViews() {
        emulationTextView = (TextView) findViewById(R.id.textViewEmu);
    }

    @Override
    protected void onPause() {
        eNfcAdapter.disableForegroundDispatch(this);

        super.onPause();
    }

}
