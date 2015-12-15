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
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.ag.mk.nfccardreadwrite.dialogs.SettingsDialog;
import com.ag.mk.nfccardreadwrite.tools.AddressBookWriter;
import com.ag.mk.nfccardreadwrite.tools.DataWork;
import com.ag.mk.nfccardreadwrite.tools.VCardFormatTool;
import com.ag.mk.nfccardreadwrite.addons.Vibration;
import com.ag.mk.nfccardreadwrite.addons.Voice;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback, TextToSpeech.OnInitListener{

    public static final String TAG = "Nfc Card App";

    private TextView isVCardTextView;

    private ListView vCardListView;

    private Button contactExportButton, contactImportButton, createVCardActvivityButton, androidBeamButton;

    private NfcAdapter nfcAdapter;

    private ArrayAdapter<String> adapter;

    private TextToSpeech textToSpeech;

    private IntentFilter[] intentFilters;
    private String[][] techLists;

    private CardWriter cardWriter = new CardWriter(null);
    private Voice voice;

    private ArrayList<String> cardContent = null;

    private ContactListDialog contactListDialog;
    private SettingsDialog settingsDialog;

    private String vCardInformation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        initImportedClasses();
        initButtons();
        initTextViews();
        initListViews();

        loadSettings();

        checkNFCSupport();
        checkNFC();

        initIntentFilter();

        handleIntent(getIntent());
    }

    private void initImportedClasses() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        new Vibration((Vibrator)getSystemService(Context.VIBRATOR_SERVICE));
        Vibration.setVibration(true);

        new DataWork(this);

        textToSpeech = new TextToSpeech(this, this);
        new Voice(textToSpeech);

        contactListDialog = new ContactListDialog(this);
        settingsDialog = new SettingsDialog(this);

    }

    private void initButtons(){

        contactExportButton = (Button) findViewById(R.id.contactExportButton);
        contactExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibration.vibrate();
                AddressBookWriter.writecontact(MainActivity.this, cardContent);
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
                voice.speakOut("Bitte wählen Sie einen Kontakt aus!");
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

    private void loadSettings() {

        boolean vibration = Boolean.valueOf(DataWork.readSingleLineFile("vibration"));
        boolean voice = Boolean.valueOf(DataWork.readSingleLineFile("voice"));

        settingsDialog.getVibrationSwitch().setChecked(vibration);
        settingsDialog.getVoiceSwitch().setChecked(voice);

        Vibration.setVibration(vibration);
        Voice.setSound(voice);
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
        cardContent = VCardFormatTool.extractCardInformation(CardReader.readTag(intent).split("\r\n"));
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

        textToSpeech = new TextToSpeech(this, this);
        new Voice(textToSpeech);

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
            Voice.speakOut("Beam Modus gestartet!");
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
        NdefMessage ndefMessage = cardWriter.createNdefMessage(vCardInformation);
        Voice.speakOut("Beam vorgang läuft!");
        return ndefMessage;
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = textToSpeech.setLanguage(Locale.GERMAN);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                // speakOut("Sprachaktivierung erfolgreich!");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            settingsDialog.showDialog();
            return true;
        }else if (id == R.id.close) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
