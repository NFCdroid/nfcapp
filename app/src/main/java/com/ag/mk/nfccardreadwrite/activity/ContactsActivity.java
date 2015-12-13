package com.ag.mk.nfccardreadwrite.activity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.ag.mk.nfccardreadwrite.R;
import com.ag.mk.nfccardreadwrite.cardwork.CardWriter;
import com.ag.mk.nfccardreadwrite.tools.Dialogs;
import com.ag.mk.nfccardreadwrite.tools.VCardFormatTool;
import com.ag.mk.nfccardreadwrite.tools.Vibration;

import java.util.ArrayList;

public class ContactsActivity extends ListActivity implements NfcAdapter.CreateNdefMessageCallback {


    private ArrayList<String> listItems = new ArrayList<>();
    private String vCardInformation = null;
    private String mobilenumber = null;
    private String homenumber = null;
    private String worknumber = null;
    private String email = null;
    private Cursor cur = null;
    private Cursor pCur = null;
    //shamelessly stolen from elsewhere....
    private CardWriter cardWriter;
    private NfcAdapter nfcAdapter;

    private Button backButton;

    Dialogs dialogs = new Dialogs();

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_read_contacts);

        initButtons();


        ArrayAdapter<String> adapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        setListAdapter(adapter);

        // Check for available NFC Adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        cardWriter = new CardWriter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        displayContacts();
    }

    private void initButtons() {
        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibration.vibrate();
                finish();
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        ContentResolver cr = getContentResolver();
        cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        assert cur != null;
        cur.moveToPosition(position);

        String _id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

        // Not yet implemented in VCardFormatTool
        // This could be user to read address data..
        /*
        String street = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
        String city = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
        String state = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
        String postalCode = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
        */

        if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
            pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{_id}, null);
            if(pCur != null && pCur.getCount() > 0) {
                while (pCur.moveToNext()) {
                    String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    int type = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    // http://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Phone.html
                    // TYPE values are static int so easy to switch.
                    switch (type){
                        case 2:
                            mobilenumber = phone;
                            break;
                        case 3:
                            worknumber = phone;
                            break;
                        case 1:
                            homenumber = phone;
                            break;
                    }
                }
                pCur.close();
            }
        }

        //TODO: get email.

        vCardInformation = VCardFormatTool.getFormatedVCardString(name,mobilenumber,homenumber,email);

        dialogs.showBeamOrWriteDialog(this, vCardInformation);

        Vibration.vibrate();

    }

    private void displayContacts() {
        ContentResolver cr = getContentResolver();
        cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                listItems.add(name);
            }
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
    /*
    public void setVCardInformation(){
        vCardInformation =VCardFormatTool.getFormatedVCardString("Hans","0815","12345","hans@wurst.de");
    }*/

    public void startBeamMode(){
        nfcAdapter.setNdefPushMessageCallback(this, this);
    }

}