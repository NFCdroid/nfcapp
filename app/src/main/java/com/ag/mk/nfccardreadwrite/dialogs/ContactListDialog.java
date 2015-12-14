package com.ag.mk.nfccardreadwrite.dialogs;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.ag.mk.nfccardreadwrite.R;
import com.ag.mk.nfccardreadwrite.activity.MainActivity;
import com.ag.mk.nfccardreadwrite.tools.VCardFormatTool;
import com.ag.mk.nfccardreadwrite.tools.Vibration;

import java.util.ArrayList;

/**
 * Created by marko on 13.12.15.
 */
public class ContactListDialog {

    private ArrayList<String> listItems = new ArrayList<>();

    private String vCardInformation = null;
    private String mobilenumber = null;
    private String homenumber = null;
    private String worknumber = null;
    private String email = null;
    private Cursor cur = null;
    private Cursor pCur = null;

    public ContactListDialog(){

    }

    public void initContactDialog(final MainActivity mainActivity){

       final Dialog contactListDialog = new Dialog(mainActivity);

        LayoutInflater inflater = (LayoutInflater)mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_read_contacts, (ViewGroup)mainActivity.findViewById(R.id.contactList));
        contactListDialog.setContentView(layout);
        contactListDialog.setTitle("Kontakte");

        final ListView listView = (ListView) layout.findViewById(R.id.contactList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContentResolver cr = mainActivity.getContentResolver();
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

                System.out.println("VCARD: "+ name + " "+ mobilenumber+ " "+ homenumber+ " "+ email);

                vCardInformation = VCardFormatTool.getFormatedVCardString(name, mobilenumber, homenumber, email);

                mainActivity.setVCardInformationOnMainScreen(vCardInformation);

                contactListDialog.cancel();

                Vibration.vibrate();

            }
        });

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_list_item_1, android.R.id.text1, listItems);

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
            }
        });

        Button backButton = (Button) layout.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibration.vibrate();
                contactListDialog.cancel();
            }
        });

        contactListDialog.show();

        displayContacts(mainActivity);
    }

    private void displayContacts(MainActivity mainActivity) {
        ContentResolver cr = mainActivity.getContentResolver();
        cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cur != null) {
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    listItems.add(name);
                }
            }
        }
    }
}
