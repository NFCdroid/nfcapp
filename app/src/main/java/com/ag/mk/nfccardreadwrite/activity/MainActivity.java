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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ag.mk.nfccardreadwrite.R;
import com.ag.mk.nfccardreadwrite.addons.Vibration;
import com.ag.mk.nfccardreadwrite.addons.Voice;
import com.ag.mk.nfccardreadwrite.cardwork.CardReader;
import com.ag.mk.nfccardreadwrite.cardwork.CardWriter;
import com.ag.mk.nfccardreadwrite.dialogs.ContactListDialog;
import com.ag.mk.nfccardreadwrite.dialogs.SettingsDialog;
import com.ag.mk.nfccardreadwrite.addressbookwork.AddressBookWriter;
import com.ag.mk.nfccardreadwrite.tools.ContactTools;
import com.ag.mk.nfccardreadwrite.tools.DataWork;
import com.ag.mk.nfccardreadwrite.tools.NfcTools;
import com.ag.mk.nfccardreadwrite.tools.VCardFormatTools;

import java.util.ArrayList;
import java.util.Locale;

/**
 *@author Marko Klepatz, Oliver Friedrich
 */
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
    private ContactTools contactTools;
    private NfcTools nfcTools;

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

        nfcTools.checkNFCSupport();
        nfcTools.checkNFC();

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
        contactTools = new ContactTools(this);
        nfcTools = new NfcTools(this, nfcAdapter);

    }

    private void initButtons(){

        contactExportButton = (Button) findViewById(R.id.contactExportButton);
        contactExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibration.vibrate();
                AddressBookWriter.writeContact(MainActivity.this, cardContent);
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
                Voice.speakOut("Bitte wählen Sie einen Kontakt aus!");
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
        vCardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    contactTools.easterEgg(cardContent.get(0).split(" ")[1]);
                } else if (position == 1 || position == 2) {
                    contactTools.callContact(cardContent.get(0).split(" ")[1], cardContent.get(position).split(" ")[1]);
                } else if (position == 3) {
                    contactTools.mailContact(cardContent.get(position).split(" ")[1]);
                }
            }
        });
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
            Voice.speakOut("Ein neuer Kontakt wurde eingelesen.");

        }else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {

            setCardContentFromIntent(intent);
            fillVCardListView(cardContent);
            Voice.speakOut("NFC Chip erkannt.");
        }
    }

    private void setCardContentFromIntent(Intent intent){

        String tempCardInformation = CardReader.readTag(intent);
        if(tempCardInformation!= null) {
            cardContent = VCardFormatTools.extractCardInformation(tempCardInformation.split("\r\n"));
        }
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
        //        IsoDep.class.getName(), Warum zum Geier ist diese Klasse dafür verantwortlich dafür das das verdammte teil ständig neu startet???!!!
                MifareUltralight.class.getName(),
                Ndef.class.getName()}
                };
    }

    public void setVCardInformationOnMainScreen(String vCardInformation) {
        this.vCardInformation = vCardInformation;
        cardContent = VCardFormatTools.extractCardInformation(vCardInformation.split("\r\n"));
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

            int result = textToSpeech.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
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
