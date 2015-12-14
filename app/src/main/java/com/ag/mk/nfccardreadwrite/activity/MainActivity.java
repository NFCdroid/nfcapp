package com.ag.mk.nfccardreadwrite.activity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
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
import android.os.Parcelable;
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

    private List<String> cardContent = null;

    private ContactListDialog contactListDialog;

    private String vCardInformation = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        cardReader = new CardReader(this);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        initTextViews();
        initListViews();
        initButtons();

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


        //Toast.makeText(this, "NFC Received!", Toast.LENGTH_SHORT).show();

        //Toast.makeText(this, "MSG: " + cardReader.readTag(intent), Toast.LENGTH_SHORT);
        //Log.i(TAG, "Intent received" + cardReader.readTag(intent));

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {

            Log.i(TAG, "Discovered tag with intent: " + intent);



            fillVCardListView(VCardFormatTool.extractCardInformation(processIntent(intent).split("\r\n")));

            //TODO: Hier kommmt die Logik zum handeln hin


        }else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            //processIntent(getIntent());
            fillVCardListView(VCardFormatTool.extractCardInformation(processIntent(intent).split("\r\n")));
        }

    }

    private void initTextViews(){

        isVCardTextView = (TextView) findViewById(R.id.startTextView);

    }

    private void initListViews(){

        vCardListView = (ListView)findViewById(R.id.vCardlistView);

    }

    private void fillVCardListView(List<String> cardContent){


        //cardReader.readTag(intent).split("\r\n")); // liest Tag spaltet es auf und ruft karteninformationsextraktionsmethode auf

       // adapter.notifyDataSetChanged();
       // vCardListView.invalidate();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, cardContent);
        vCardListView.setAdapter(adapter);

        if(vCardListView.getVisibility() == View.GONE){
            vCardListView.setVisibility(View.VISIBLE);
            isVCardTextView.setVisibility(View.GONE);
        }

    }

    private void initButtons(){

        //TODO einerfliegt noch raus! -- Klaus: Sehe ich nicht so.

        //Hier export in die Kontakte
        contactsActivityButton = (Button) findViewById(R.id.ContactActivityButton);
        contactsActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactWrite.writecontact(MainActivity.this, cardContent);
            }
        });

        createVCardActvivityButton = (Button) findViewById(R.id.createVCardActvivityButton);
        createVCardActvivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateVCardActivity.class);
                if(cardContent!= null) {
                    intent.setAction(AppWidgetManager.EXTRA_CUSTOM_EXTRAS);
                    intent.putExtra("vci", cardContent.get(0) + ";" + cardContent.get(1) + ";" + cardContent.get(2) + ";" + cardContent.get(3));
                }
                startActivity(intent);
            }
        });

        //Hier Import aus Kontakten
        contactImportButton = (Button)findViewById(R.id.contactImportButton);
        contactImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogs.initContactDialog(MainActivity.this);
            }
        });

        androidBeamButton = (Button)findViewById(R.id.androidBeamButton);
        androidBeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBeamMode();
                Toast.makeText(MainActivity.this, "Beam Modus gestartet...", Toast.LENGTH_SHORT);
            }
        });

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

    private String processIntent(Intent intent) {

        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        String result = new String(msg.getRecords()[0].getPayload());
        return  result;
    }


    private void startBeamMode(){
        nfcAdapter.setNdefPushMessageCallback(this, this);
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

    public void setVCardInformationOnMainScreen(String vCardInformation) {
        this.vCardInformation = vCardInformation;
        cardContent = VCardFormatTool.extractCardInformation(vCardInformation.split("\r\n"));
        fillVCardListView(cardContent);
    }

}
