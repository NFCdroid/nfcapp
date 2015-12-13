package com.ag.mk.nfccardreadwrite.activity;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.ag.mk.nfccardreadwrite.R;
import com.ag.mk.nfccardreadwrite.cardwork.CardWriter;
import com.ag.mk.nfccardreadwrite.tools.VCardFormatTool;
import java.util.ArrayList;

public class ReadContactsActivity extends ListActivity {


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
    private NfcAdapter mNfcAdapter;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_read_contacts);
        ArrayAdapter<String> adapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        setListAdapter(adapter);

        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        cardWriter = new CardWriter(this);

        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        displayContacts();
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

        Cursor emails = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + _id, null, null);
        if (emails != null) {
            while (emails.moveToNext()) {
                email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            }
        }
        emails.close();

        vCardInformation = VCardFormatTool.getFormatedVCardString(name,mobilenumber,homenumber,email);
        //TODO: implement beam/write here

    }

    private void displayContacts() {
        ContentResolver cr = getContentResolver();
        cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cur != null)
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    listItems.add(name);
                }
            }
    }


}