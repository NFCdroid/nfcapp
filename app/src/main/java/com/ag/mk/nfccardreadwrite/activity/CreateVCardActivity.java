package com.ag.mk.nfccardreadwrite.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ag.mk.nfccardreadwrite.R;
import com.ag.mk.nfccardreadwrite.addons.Vibration;
import com.ag.mk.nfccardreadwrite.addons.Voice;
import com.ag.mk.nfccardreadwrite.cardwork.CardWriter;
import com.ag.mk.nfccardreadwrite.tools.VCardFormatTools;

import java.util.ArrayList;

/**
 * Diese Activity beinhaltet die Logik zu den GUI Elementen,
 * die benötigt werden um NFC Medien zu beschrieben.<br><br>
 *
 * Zusätzlich beinhaltet sie eine Methode zum einlesen gülter NFC Medien.
 *
 * @author Marko Klepatz, Olivier Friedrich
 */
public class CreateVCardActivity extends AppCompatActivity {

    private Button backButton, writeVCardButton, clearButton;

    private EditText userNameEditText, mobileNumberEditText, homeNumberEditText, eMailEditText;

    private Intent intent = new Intent();

    private CardWriter cardWriter;

    private NfcAdapter nfcAdapter;

    private IntentFilter[] intentFilters;
    private String[][] techLists;

    /**
     * Diese Methode setzt alle relevanten Eigentschaften für die GUI
     * und leitet die Initialisierungen aller Objekte ein.<br><br>
     *
     * Zusätzlich nimmt sie den aufrufenden Intent von der MainActivty entgegen
     * und Prüft ob dieser Daten zum beschreiben auf ein NFC Medium enthält und leitet diese weiter,
     * an die GUI Objekte.
     *
     * @param savedInstanceState
     *
     * @see NfcAdapter
     * @see CardWriter
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_vcard);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        cardWriter = new CardWriter(this);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        initIntentFilter();

        initButtons();
        initTextFields();

        //Prüft ob Kontakt Daten enthalten sind
        if(getIntent().hasExtra("vci")) {
            fillTextFields(getIntent().getStringArrayListExtra("vci"));
        }

    }

    /**
     * Diese Methode inititialisiert alle Button Objekte und deren Logik.
     */
    private void initButtons(){

        clearButton = (Button) findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibration.vibrate();
                userNameEditText.setText("");
                mobileNumberEditText.setText("");
                homeNumberEditText.setText("");
                eMailEditText.setText("");
            }
        });

        backButton = (Button) findViewById(R.id.backToMainAcrivtyButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibration.vibrate();
                finish();
            }
        });

        writeVCardButton = (Button) findViewById(R.id.writeVCardButton);
        writeVCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Vibration.vibrate();

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                if (tag != null) {

                    NdefMessage ndefMessage = cardWriter.createNdefMessage(
                            VCardFormatTools.getFormatedVCardString(
                                    userNameEditText.getText().toString().replace("Name: ", ""),
                                    mobileNumberEditText.getText().toString().replace("Telefon-Mobil: ", ""),
                                    homeNumberEditText.getText().toString().replace("Telefon-Festnetz: ", ""),
                                    eMailEditText.getText().toString().replace("E-Mail: ", "")));
                    cardWriter.writeNdefMessage(tag, ndefMessage);


                    //TODO: Weiter Testen
                    //Nur Name und Handynummer sind zwingend
                    String name = userNameEditText.getText().toString();
                    String mobileNumber = mobileNumberEditText.getText().toString();

                    if (name.matches("") || mobileNumber.matches(""))
                        Toast.makeText(CreateVCardActivity.this, "Text Field is empty!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(CreateVCardActivity.this, "Bitte Karte auflegen!", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    /**
     * Diese Methode inititialisiert alle TextField Objekte.
     */
    private void initTextFields(){

        userNameEditText = (EditText) findViewById(R.id.userNameEditText);
        mobileNumberEditText = (EditText) findViewById(R.id.telefonMobilEditText);
        homeNumberEditText = (EditText) findViewById(R.id.telefonFestnetzEditText);
        eMailEditText = (EditText) findViewById(R.id.eMailEditText);

    }

    /**
     * Diese Methode empfängt einen von dem Manifest gefilterten Intent,
     * welcher nur dann in dieser ankommt wenn das NFC Medium,
     * was an das Gerät gehalten wird, mit den gültigen Technologien ausgestattet ist.<br><br>
     *
     * Die aktuell gültigen Technologien sind in folgenden Dateien einsehbar:<br>
     * <b>AndroidManifest.xml</b><br>
     * <b>tech.xml</b>
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {

        Toast.makeText(CreateVCardActivity.this,"Karte erkannt!", Toast.LENGTH_SHORT).show();
        Voice.speakOut("Es wurde ein zulässiges Medium erkannt!");

        this.intent = intent;

        super.onNewIntent(intent);
    }

    /**
     * Diese Methode initialisiert den Intentfilter für den NFC Adapter.
     * Zusätzlich zum Manifest-Filter überprüft dieser auch noch einmal,
     * ob die richitgen Technologien gefiltert wurden.
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

    /**
     * Diese Methode befüllt alle Textfelder mit den Kontakt-Daten die von der MainActivity gesendet wurden.
     *
     * @param cardContent übergibt die Kontakt daten von der MainActivtity
     */
    private void fillTextFields(ArrayList<String> cardContent){
        userNameEditText.setText(cardContent.get(0));
        mobileNumberEditText.setText(cardContent.get(1));
        homeNumberEditText.setText(cardContent.get(2));
        eMailEditText.setText(cardContent.get(3));

    }

    /**
     * Diese Methode konfiguriert und aktiviert bei Aufruf den NFC Adapter.
     */
    @Override
    protected void onResume() {

        Intent intent = new Intent(this, CreateVCardActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
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
}
