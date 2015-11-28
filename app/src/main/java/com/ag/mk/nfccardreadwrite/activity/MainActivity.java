package com.ag.mk.nfccardreadwrite.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ag.mk.nfccardreadwrite.R;
import com.ag.mk.nfccardreadwrite.cardwork.CardReader;
import com.ag.mk.nfccardreadwrite.cardwork.CardWriter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Nfc Card App";

    private ListView vCardListView;

    private Button emulatorButton, contactImportButton, createVCardActvivityButton;

    private NfcAdapter nfcAdapter;

    private ArrayAdapter<String> adapter;

    private IntentFilter[] intentFilters;
    private String[][] techLists;

    private CardReader cardReader;
    private CardWriter cardWriter;

    private Intent intent;

    private List<String> cardList = new ArrayList<String>();
    String[] values = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardReader = new CardReader(this);
        cardWriter = new CardWriter(this);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        initTextViews();
        iniListViews();
        initButtons();
        initEditTexts();

        checkNFCSupport();

        checkNFC();

        initIntentFilter();

        handleIntent(getIntent());
    }

    /**Methode um den eintreffenden Intent zu handhaben.
     * 
     * @param intent empfangener NFC Intent --> enthält alle gelesenen Daten auf der Karte (insofern auslesbar)
     */
    private void handleIntent(Intent intent) {

        Log.i(TAG, "Intent received");
        Toast.makeText(this, "NFC Received!", Toast.LENGTH_SHORT).show();

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {

            this.intent = intent;

            Log.i(TAG, "Discovered tag with intent: " + intent);

            fillVCardListView(intent);

            //TODO: Hier kommmt die Logik zum handeln hin


        }

    }
    
    private void initTextViews(){

    }

    private void iniListViews(){

        vCardListView = (ListView)findViewById(R.id.vCardlistView);


        values[0] = "Name: Max";
        values[1] = "Telefon-Mobil: 012345678910";
        values[2] = "Telefon-Festnetz: 09876543210";
        values[3] = "E-Mail: max@mustermail.net";





        /*for (int i=0; i<values.length;i++){
            values[i] = deviceList.get(i);

        };
        */
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);

        vCardListView.setAdapter(adapter);

        //istView.invalidateViews();
    }

    private void fillVCardListView(Intent intent){

        if(cardList.size()>0){

            values = new String[cardList.size()+1];


            for(int i= 0; i<cardList.size(); i++){
                try {
                    values[i] = cardList.get(i);
                    System.out.println("CARDLIST: " + i + " INHALT: " + cardList.get(i));
                }catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }

            values[cardList.size()] = cardReader.readTag(intent);

        }else{

            values[0] = cardReader.readTag(intent);

        }
       // adapter.notifyDataSetChanged();

        cardList.add(values[0]);
       // vCardListView.invalidate();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        vCardListView.setAdapter(adapter);

    }

    private void initButtons(){

        emulatorButton = (Button) findViewById(R.id.emulatorButton);
        emulatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,EmulatorActivity.class);
                startActivity(intent);
            }
        });

        createVCardActvivityButton = (Button) findViewById(R.id.CreateVCardActvivityButton);
        createVCardActvivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateVCardActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initEditTexts(){

    }


    /**
     * Initialisiert den Intent Filter für den NFC Intent.
     * Initialisiert die Array Liste für die unterstützten NFC Technologien für den NFC Adapter.
     * 
     * Hinweis: Die Technologien werden hier noch nicht dem NFC Adapter hinzugefügt
     * dies geschiet in der Metode onResume()
     */
    private void initIntentFilter() {
        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        intentFilters = new IntentFilter[] { tech, };
        
        // String Array wird mit den Namen der Technologien gefüllt.
        techLists = new String[][] { new String[] {
                NfcA.class.getName(),
                MifareUltralight.class.getName(),
                Ndef.class.getName()}
                };
    }

    private void checkNFC() {
        if (nfcAdapter.isEnabled()) {
            Log.i(TAG, "NFC is enabled.");
        } else {
            Log.i(TAG, "NFC is disabled.");
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
