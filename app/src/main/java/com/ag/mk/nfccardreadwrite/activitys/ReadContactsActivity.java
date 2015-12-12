package com.ag.mk.nfccardreadwrite.activitys;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.ContactsContract;
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
    private String email = null;
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
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
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
            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + _id, null, null);
            while (pCur.moveToNext()) {
                int type = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                switch (type) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        String homenumber = pCur.getString(pCur.getColumnIndex(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME)));
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        String mobilenumber = pCur.getString(cur.getColumnIndex(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)));
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                        // not yet implemented in VCardFormatTool
                        // String worknumber = pCur.getString(cur.getColumnIndex(String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_WORK)));
                }
            }
            pCur.close();
        }

        vCardInformation = VCardFormatTool.getFormatedVCardString(name,mobilenumber,homenumber,email);
        //TODO: implement beam/write here

    }

    private void displayContacts() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                listItems.add(name);
            }
        }
    }


}