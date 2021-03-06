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
import com.ag.mk.nfccardreadwrite.tools.BeamTools;
import com.ag.mk.nfccardreadwrite.tools.ContactTools;
import com.ag.mk.nfccardreadwrite.addons.DataWork;
import com.ag.mk.nfccardreadwrite.tools.NfcTools;
import com.ag.mk.nfccardreadwrite.tools.VCardFormatTools;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Diese Activity beinhaltet die Logik zu den GUI Elementen,
 * die benötigt werden um NFC Medien zu lesen und anzuzeigen.<br>
 * Zusätzlich wird hier das Anzeigen von Kontakten aus dem Adressbuch
 * und das Importieren von neuen Kontakten,
 * sowie die Android Beam-Funktion eingeleitet.
 * Ebenso wird hier auch die CreateVCardActivity gerufen.<br><br>
 * Weiterführend wird immer diese Activity gerufen,
 * wenn eine für diese App gültige NFC Technologie
 * oder der spezielle Mime Type dieser App erkannt wird.
 * (Der Mime Type ist in der Klasse <b>CardWriter</b> einsehbar)
 *
 * @author Marko Klepatz, Oliver Friedrich
 * @see CardWriter
 */
public class MainActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback, TextToSpeech.OnInitListener {

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
    private BeamTools beamTools;
    private ContactTools contactTools;
    private NfcTools nfcTools;

    private ArrayList<String> cardContent = null;

    private ContactListDialog contactListDialog;
    private SettingsDialog settingsDialog;

    private String vCardInformation = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * Diese Methode setzt alle relevanten Eigenschaften für die GUI
     * und leitet die Initialisierungen aller Objekte ein.<br><br>
     * Zusätzlich nimmt diese Methode einen von dem Manifest gefilterten Intent entgegen,
     * welcher nur dann in dieser ankommt, wenn das NFC Medium,
     * was an das Gerät gehalten wird, mit den gültigen Technologien ausgestattet ist, bzw.
     * den Mime Type für diese App besitzt.<br><br>
     * Die aktuell gültigen Technologien sind in folgenden Dateien einsehbar:<br>
     * <b>AndroidManifest.xml</b><br>
     * <b>tech.xml</b>
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * Diese Methode initialisiert alle zusätzlich importierten Klassen.
     *
     * @see Vibration
     * @see DataWork
     * @see TextToSpeech
     * @see Voice
     * @see ContactListDialog
     * @see SettingsDialog
     * @see ContactListDialog
     * @see NfcTools
     * @see NfcAdapter
     * @see BeamTools
     */
    private void initImportedClasses() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        new Vibration((Vibrator) getSystemService(Context.VIBRATOR_SERVICE));
        Vibration.setVibration(true);

        new DataWork(this);

        textToSpeech = new TextToSpeech(this, this);
        new Voice(textToSpeech);

        contactListDialog = new ContactListDialog(this);
        settingsDialog = new SettingsDialog(this);
        contactTools = new ContactTools(this);
        nfcTools = new NfcTools(this, nfcAdapter);
        beamTools = new BeamTools(this);
    }

    /**
     * Diese Methode initialisiert alle Button Objekte und deren Logik.
     */
    private void initButtons() {

        contactExportButton = (Button) findViewById(R.id.contactExportButton);
        contactExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibration.softVibrate();
                AddressBookWriter.writeContact(MainActivity.this, cardContent);
            }
        });

        createVCardActvivityButton = (Button) findViewById(R.id.createVCardActvivityButton);
        createVCardActvivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibration.softVibrate();
                startCreateVCardActivityIntent();

            }
        });

        contactImportButton = (Button) findViewById(R.id.contactImportButton);
        contactImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Voice.speakOut("Bitte wählen Sie einen Kontakt aus!");
                Vibration.softVibrate();
                contactListDialog.showDialog();
            }
        });

        androidBeamButton = (Button) findViewById(R.id.androidBeamButton);
        androidBeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibration.softVibrate();

                //Prüfe ob bereits ein Kontakt im Textview steht
                if(cardContent != null) {
                    beamTools.startBeamMode(nfcAdapter);
                }else{
                    Toast.makeText(MainActivity.this, "Es sind leider keine Kontaktdaten zum Beamen vorhanden.\nBitte erst einen Kontakt anlegen/importieren.", Toast.LENGTH_SHORT).show();
                    Voice.speakOut("Es sind leider keine Kontaktdaten zum Biehmen vorhanden. Bitte erst einen Kontakt anlegen/importieren.");
                }
            }
        });

    }

    /**
     * Diese Methode initialisiert alle TextView Objekte.
     */
    private void initTextViews() {
        isVCardTextView = (TextView) findViewById(R.id.startTextView);
    }

    /**
     * Diese Methode initialisiert alle ListView Objekte und deren Logik.
     */
    private void initListViews() {
        vCardListView = (ListView) findViewById(R.id.vCardlistView);
        vCardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    contactTools.easterEgg(cardContent.get(0).split(" ")[1]);
                } else if (position == 1 || position == 2) {

                    String tempNumber = cardContent.get(position);

                    if(tempNumber.contains("Telefon-Mobil:")) {
                        tempNumber = tempNumber.replace("Telefon-Mobil:", "").trim();
                    }else if(tempNumber.contains("Telefon-Festnetz:")) {
                        tempNumber = tempNumber.replace("Telefon-Festnetz:", "").trim();
                    }

                    contactTools.callContact(cardContent.get(0).split(" ")[1], tempNumber);
                } else if (position == 3) {
                    contactTools.mailContact(cardContent.get(position).split(" ")[1]);
                }
            }
        });
    }

    /**
     * Diese Methode lädt alle Einstellungen die im SettingsDialog gespeichert wurden.
     *
     * @see ContactListDialog
     * @see DataWork
     */
    private void loadSettings() {

        boolean vibration = Boolean.valueOf(DataWork.readSingleLineFile("vibration"));
        boolean voice = Boolean.valueOf(DataWork.readSingleLineFile("voice"));

        settingsDialog.getVibrationSwitch().setChecked(vibration);
        settingsDialog.getVoiceSwitch().setChecked(voice);

        Vibration.setVibration(vibration);
        Voice.setSound(voice);
    }

    /**
     * Diese Methode filtert und leitet alle eintreffenden Intents weiter,
     * an die Methoden zum Anzeigen von Kontakten.
     *
     * @param intent übergibt empfangenen NFC Intent, dieser enthält alle gelesenen Daten auf dem NFC Medium (insofern auslesbar)
     */
    private void handleIntent(Intent intent) {

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {

            setCardContentFromIntent(intent);
            fillVCardListView(cardContent);
            Voice.speakOut("Ein neuer Kontakt wurde eingelesen.");
            Vibration.hardVibrate();

        } else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {

            setCardContentFromIntent(intent);
            fillVCardListView(cardContent);
            Voice.speakOut("NFC Chip erkannt.");
            Vibration.hardVibrate();
        }
    }

    /**
     * Diese Methode weist der Liste <b>cardContent</b> den Inhalt des NFC Mediums,
     * unter Zuhilfenahme der beiden Methoden <b>readTag</b> aus der Klasse <b>CardReader</b>
     * und <b>extractCardInformation</b> aus der Klasse <b>VCardFormatTools</b> zu.
     *
     * @param intent übergibt den auszulesenden Intent
     * @see CardReader
     * @see VCardFormatTools
     */
    private void setCardContentFromIntent(Intent intent) {

        String tempCardInformation = CardReader.readTag(intent);
        if (tempCardInformation != null) {
            cardContent = VCardFormatTools.extractCardInformation(tempCardInformation.split("\r\n"));
        }
    }

    /**
     * Diese Methode befüllt die <b>vCardListView</b> mit dem Inhalt des NFC Mediums
     * und ruft die Methode <b>setCardListViewVisible</b> zum sichtbar machen dieser ListView auf.
     *
     * @param cardContent übergibt den Inhalt des NFC Mediums
     */
    private void fillVCardListView(ArrayList<String> cardContent) {

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, cardContent);
        vCardListView.setAdapter(adapter);

        setCardListViewVisible();

    }

    /**
     * Diese Methode macht die vCardListView sichtbar und die isVCardTextView unsichtbar.
     */
    private void setCardListViewVisible() {
        if (vCardListView.getVisibility() == View.GONE) {
            vCardListView.setVisibility(View.VISIBLE);
            isVCardTextView.setVisibility(View.GONE);
        }
    }

    /**
     * Diese Methode generiert einen Intent zum Starten der <b>CreateVCardActivity</b>.
     * Wenn die <b>cardContent</b> Liste befüllt ist, werden diese Daten mitgeschickt,
     * um diese dann direkt auf ein NFC-fähiges Medium schreiben zu können.
     */
    private void startCreateVCardActivityIntent() {
        Intent intent = new Intent(MainActivity.this, CreateVCardActivity.class);

        if (cardContent != null) {
            intent.setAction(AppWidgetManager.EXTRA_CUSTOM_EXTRAS);
            intent.putStringArrayListExtra("vci", cardContent);
        }
        startActivityForResult(intent, 0);
    }


    /**
     * Diese Methode initialisiert den Intentfilter für den NFC Adapter.
     * Zusätzlich zum Manifest-Filter überprüft dieser auch noch einmal,
     * ob die richtigen Technologien gefiltert wurden.
     */
    private void initIntentFilter() {
        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        intentFilters = new IntentFilter[]{tech,};

        // String Array wird mit den Namen der Technologien gefüllt.
        techLists = new String[][]{new String[]{
                NfcA.class.getName(),
                //        IsoDep.class.getName(), Warum zum Geier ist diese Klasse dafür verantwortlich dafür das das verdammte teil ständig neu startet???!!!
                MifareUltralight.class.getName(),
                Ndef.class.getName()}
        };
    }

    /**
     * Diese Methode lädt die Daten auf die <b>vCardListView</b>.
     *
     * @param vCardInformation übergibt die Informationen die sich auf dem NFC Medium befinden.
     */
    public void setVCardInformationOnMainScreen(String vCardInformation) {
        this.vCardInformation = vCardInformation;
        cardContent = VCardFormatTools.extractCardInformation(vCardInformation.split("\r\n"));
        fillVCardListView(cardContent);
    }

    /**
     * Diese Methode empfängt einen von dem Manifest gefilterten Intent,
     * welcher nur dann in dieser ankommt, wenn das NFC Medium,
     * was an das Gerät gehalten wird, mit den gültigen Technologien ausgestattet ist.<br><br>
     * Die aktuell gültigen Technologien sind in folgenden Dateien einsehbar:<br>
     * <b>AndroidManifest.xml</b><br>
     * <b>tech.xml</b>
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);

        super.onNewIntent(intent);
    }

    /**
     * Diese Methode konfiguriert und aktiviert bei Aufruf den NFC Adapter.
     */
    @Override
    protected void onResume() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        //IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, techLists);



        super.onResume();
    }

    /**
     * Diese Methode deaktiviert den NFC Adapter.
     */
    @Override
    protected void onPause() {
        nfcAdapter.disableForegroundDispatch(this);

        super.onPause();
    }

    /**
     * Diese Callback Methode gibt die zu sendende NDEF Message für den Android NFC Beamer zurück.
     *
     * @param event
     * @return gibt die zu sendende NDEF Message für den Android NFC Beamer zurück.
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        NdefMessage ndefMessage = cardWriter.createNdefMessage(vCardInformation);

        Voice.speakOut("Beam vorgang läuft!");
        return ndefMessage;
    }

    /**
     * Diese Methode weist der TextToSpeech Klasse die Sprache zu,
     * mit welcher dann die Sprachausgabe erfolgt.
     * In diesem Fall wird immer die Standardsprache des Geräts verwendet.
     *
     * @param status
     */
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

    /**
     * Diese Methode beendet beim Aufruf die TextToSpeech Vorgang/Funktion.
     */
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {

            Bundle returnedData = data.getExtras();

            if(returnedData.get("kdata")!= null){
                this.cardContent = returnedData.getStringArrayList("kdata");

                this.vCardInformation = VCardFormatTools.getFormatedVCardString(cardContent.get(0).replace("Name: ", ""), cardContent.get(1).replace("Telefon-Mobil: ", ""), cardContent.get(2).replace("Telefon-Festnetz: ", ""), cardContent.get(3).replace("E-Mail: ", ""));

                    setVCardInformationOnMainScreen(vCardInformation);

            }
        }
    }
}
