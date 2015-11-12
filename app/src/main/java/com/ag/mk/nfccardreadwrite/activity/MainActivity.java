package com.ag.mk.nfccardreadwrite.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.ag.mk.nfccardreadwrite.cardwork.CardReader;
import com.ag.mk.nfccardreadwrite.cardwork.CardWriter;
import com.ag.mk.nfccardreadwrite.R;

public class MainActivity extends Activity {

    public static final String TAG = "Nfc Card App";

    private TextView nfcFunctionTextView, nfcOutputTextView;
    private Button writeButton,emulatorButton;
    private EditText inputEditText;

    private NfcAdapter nfcAdapter;

    private int countIntents = 0;

    private IntentFilter[] intentFilters;
    private String[][] techLists;

    private CardReader cardReader;
    private CardWriter cardWriter;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardReader = new CardReader(this);
        cardWriter = new CardWriter(this);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        initTextViews();
        initButtons();
        initEditTexts();

        checkNFCSupport();

        checkNFC();

        initIntentFilter();

        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {

        Log.i(TAG, "Intent received");
        Toast.makeText(this, "NFC Received!", Toast.LENGTH_SHORT).show();

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {

            this.intent = intent;

           // Toast.makeText(this, "NFC Received!", Toast.LENGTH_SHORT).show();
            countIntents++;
            nfcOutputTextView.setText("NFC Intent Received: " + countIntents);

            Log.i(TAG, "Discovered tag with intent: " + intent);

            nfcOutputTextView.setText(cardReader.readTag(intent));

        }

    }

    private void initTextViews(){
        nfcFunctionTextView = (TextView) findViewById(R.id.nfcFunctionTextView);
        nfcOutputTextView = (TextView) findViewById(R.id.nfcOutputTextView);
    }

    private void initButtons(){
        writeButton = (Button) findViewById(R.id.writeButton);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                if(tag != null){

                    if(!inputEditText.getText().toString().equals("")){
                        NdefMessage ndefMessage = cardWriter.createNdefMessage(inputEditText.getText().toString());
                        cardWriter.writeNdefMessage(tag, ndefMessage);
                        inputEditText.getText().clear();
                    }else {
                        Toast.makeText(MainActivity.this,"Text Field is empty!",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    nfcOutputTextView.setText("Bitte Karte auflegen!");
                }

            }
        });

        emulatorButton = (Button) findViewById(R.id.emulatorButton);
        emulatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,EmulatorActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initEditTexts(){
        inputEditText = (EditText) findViewById(R.id.inputEditText);
    }

    private void initIntentFilter() {
        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        intentFilters = new IntentFilter[] { tech, };
        techLists = new String[][] { new String[] {
                NfcA.class.getName(),
                MifareUltralight.class.getName(),
                Ndef.class.getName()}
                };
    }

    private void checkNFC() {
        if (nfcAdapter.isEnabled()) {
            nfcFunctionTextView.setText("NFC is enabled.");
        } else {
            nfcFunctionTextView.setText("NFC is disabled.");
        }
    }

    private void checkNFCSupport() {
        if (nfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);

        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        //IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, techLists);

        super.onResume();
    }

    @Override
    protected void onPause() {
        nfcAdapter.disableForegroundDispatch(this);

        super.onPause();
    }

}
