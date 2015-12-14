package com.ag.mk.nfccardreadwrite.activity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ag.mk.nfccardreadwrite.R;
import com.ag.mk.nfccardreadwrite.cardwork.CardReader;
import com.ag.mk.nfccardreadwrite.cardwork.CardWriter;
import com.ag.mk.nfccardreadwrite.dialogs.ContactListDialog;
import com.ag.mk.nfccardreadwrite.tools.ContactWrite;
import com.ag.mk.nfccardreadwrite.tools.VCardFormatTool;
import com.ag.mk.nfccardreadwrite.tools.Vibration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback{

    public static final String TAG = "Nfc Card App";

    private TextView isVCardTextView;

    private ListView vCardListView;

    private Button contactsActivityButton, contactImportButton, createVCardActvivityButton, androidBeamButton;

    private NfcAdapter nfcAdapter;

    private ArrayAdapter<String> adapter;

    private IntentFilter[] intentFilters;
    private String[][] techLists;

    private CardReader cardReader;
    private CardWriter cardWriter = new CardWriter(null);

    private ArrayList<String> cardContent = null;

    private ContactListDialog contactListDialog;

    private String vCardInformation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        new Vibration((Vibrator)getSystemService(Context.VIBRATOR_SERVICE));
        Vibration.setVibration(true);

        cardReader = new CardReader(this);
        contactListDialog = new ContactListDialog(this);

        initButtons();
        initTextViews();
        initListViews();

        checkNFCSupport();

        checkNFC();

        initIntentFilter();

        handleIntent(getIntent());
    }

    private void initButtons(){

        contactsActivityButton = (Button) findViewById(R.id.ContactActivityButton);
        contactsActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibration.vibrate();
                ContactWrite.writecontact(MainActivity.this, cardContent);
            }
        });

        createVCardActvivityButton = (Button) findViewById(R.id.createVCardActvivityButton);
        createVCardActvivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibration.vibrate();
                startCreateVCardActivityIntent();
            }
        });

        contactImportButton = (Button)findViewById(R.id.contactImportButton);
        contactImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibration.vibrate();
                contactListDialog.showDialog();
            }
        });

        androidBeamButton = (Button)findViewById(R.id.androidBeamButton);
        androidBeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibration.vibrate();
                startBeamMode();
            }
        });

    }

    private void initTextViews(){
        isVCardTextView = (TextView) findViewById(R.id.startTextView);
    }

    private void initListViews(){
        vCardListView = (ListView)findViewById(R.id.vCardlistView);
    }

    /**Methode um den eintreffenden Intent zu handhaben.
     *
     * @param intent empfangener NFC Intent --> enthält alle gelesenen Daten auf der Karte (insofern auslesbar)
     */
    private void handleIntent(Intent intent) {

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {

            setCardContentFromIntent(intent);
            fillVCardListView(cardContent);

        }else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {

            setCardContentFromIntent(intent);
            fillVCardListView(cardContent);
        }

    }

    private void setCardContentFromIntent(Intent intent){
        cardContent = VCardFormatTool.extractCardInformation(cardReader.processIntent(intent).split("\r\n"));
    }

    private void fillVCardListView(ArrayList<String> cardContent){

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, cardContent);
        vCardListView.setAdapter(adapter);

        if(vCardListView.getVisibility() == View.GONE){
            vCardListView.setVisibility(View.VISIBLE);
            isVCardTextView.setVisibility(View.GONE);
        }

    }

    private void startCreateVCardActivityIntent() {
        Intent intent = new Intent(MainActivity.this, CreateVCardActivity.class);

        if(cardContent!= null) {
            intent.setAction(AppWidgetManager.EXTRA_CUSTOM_EXTRAS);
            intent.putStringArrayListExtra("vci", cardContent);
        }
        startActivity(intent);
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
                IsoDep.class.getName(),
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

    public void setVCardInformationOnMainScreen(String vCardInformation) {
        this.vCardInformation = vCardInformation;
        cardContent = VCardFormatTool.extractCardInformation(vCardInformation.split("\r\n"));
        fillVCardListView(cardContent);
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

    private void startBeamMode(){
        // Prüfe ob Beam aktiv ist
        if (nfcAdapter.isNdefPushEnabled()) {
            Toast.makeText(MainActivity.this, "Beam Modus gestartet...", Toast.LENGTH_SHORT).show();
            nfcAdapter.setNdefPushMessageCallback(this, this);
        } else {
            Toast.makeText(MainActivity.this, "Beam nicht aktiviert!\nBitte Beam in den Settings aktivieren.", Toast.LENGTH_SHORT).show();
            //TODO: Dialog einfügen
            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            finish();
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
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
}
