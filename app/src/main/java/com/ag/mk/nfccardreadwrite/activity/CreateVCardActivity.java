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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ag.mk.nfccardreadwrite.R;
import com.ag.mk.nfccardreadwrite.cardwork.CardWriter;
import com.ag.mk.nfccardreadwrite.tools.VCardFormatTool;

import java.util.ArrayList;

public class CreateVCardActivity extends AppCompatActivity {

    private Button backButton, writeVCardButton, clearButton;

    private EditText userNameEditText, telefonMobileEditText, telefonFestnetzEditText, eMailEditText;

    private Intent intent = new Intent();

    private CardWriter cardWriter;

    private NfcAdapter nfcAdapter;

    private ArrayAdapter<String> adapter;

    private IntentFilter[] intentFilters;
    private String[][] techLists;

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

        if(getIntent().hasExtra("vci")) {
            fillTextFields(getIntent().getStringArrayListExtra("vci"));
        }

    }

    private void initButtons(){

        clearButton = (Button) findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userNameEditText.setText("");
                telefonMobileEditText.setText("");
                telefonFestnetzEditText.setText("");
                eMailEditText.setText("");
            }
        });

        backButton = (Button) findViewById(R.id.backToMainAcrivtyButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        writeVCardButton = (Button) findViewById(R.id.writeVCardButton);
        writeVCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                if (tag != null) {

                    NdefMessage ndefMessage = cardWriter.createNdefMessage(VCardFormatTool.getFormatedVCardString(userNameEditText.getText().toString(), telefonMobileEditText.getText().toString(), telefonFestnetzEditText.getText().toString(), eMailEditText.getText().toString()));
                    cardWriter.writeNdefMessage(tag, ndefMessage);


                    //TODO: Weiter Testen
                    //Nur Name und Handynummer sind zwingend
                    String uName = userNameEditText.getText().toString();
                    String mobNum = telefonMobileEditText.getText().toString();

                    if (uName.matches("") || mobNum.matches(""))
                        Toast.makeText(CreateVCardActivity.this, "Text Field is empty!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(CreateVCardActivity.this, "Bitte Karte auflegen!", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void initTextFields(){

        userNameEditText = (EditText) findViewById(R.id.userNameEditText);
        telefonMobileEditText = (EditText) findViewById(R.id.telefonMobilEditText);
        telefonFestnetzEditText = (EditText) findViewById(R.id.telefonFestnetzEditText);
        eMailEditText = (EditText) findViewById(R.id.eMailEditText);

    }

    @Override
    protected void onNewIntent(Intent intent) {

        Toast.makeText(CreateVCardActivity.this,"Karte erkannt!", Toast.LENGTH_SHORT).show();

        System.out.println("Karte erkannt!");

        this.intent = intent;

        super.onNewIntent(intent);
    }

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

    private void fillTextFields(ArrayList<String> cardContent){
        userNameEditText.setText(cardContent.get(0));
        telefonMobileEditText.setText(cardContent.get(1));
        telefonFestnetzEditText.setText(cardContent.get(2));
        eMailEditText.setText(cardContent.get(3));

    }

    @Override
    protected void onResume() {

        Intent intent = new Intent(this, CreateVCardActivity.class);
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
