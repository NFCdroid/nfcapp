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
import com.ag.mk.nfccardreadwrite.addons.Voice;
import com.ag.mk.nfccardreadwrite.tools.VCardFormatTool;
import com.ag.mk.nfccardreadwrite.addons.Vibration;

import java.util.ArrayList;

/**
 * Created by marko on 13.12.15.
 */
public class ContactListDialog {

    private ArrayList<String> listItems = new ArrayList<>();

    private String vCardInformation, mobilenumber = "---", homenumber = "---", worknumber = "---", email = "---";

    private Cursor mainCursor, phoneCursor;

    private ListView listView;

    private Button backButton;

    private Dialog contactListDialog;
    private MainActivity mainActivity;

    public ContactListDialog(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        initContactDialog();
    }

    public void initContactDialog(){

        contactListDialog = new Dialog(mainActivity);

        LayoutInflater inflater = (LayoutInflater)mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_contactlist, (ViewGroup)mainActivity.findViewById(R.id.contactList));

        contactListDialog.setContentView(layout);
        contactListDialog.setTitle("Kontakte");

        initButtons(layout);

        initListviews(layout);

    }

    private void initListviews(View layout) {
        listView = (ListView) layout.findViewById(R.id.contactList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getAdressbookData(position);

            }
        });

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_list_item_1, android.R.id.text1, listItems);

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
            }
        });
    }

    private void initButtons(View layout) {
        backButton = (Button) layout.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibration.vibrate();
                contactListDialog.cancel();
            }
        });
    }

    private void getAdressbookData(int position) {

        ContentResolver contentResolver = mainActivity.getContentResolver();
        mainCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        assert mainCursor != null;
        mainCursor.moveToPosition(position);

        String contactID = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts._ID));
        String name = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

        // Not yet implemented in VCardFormatTool
        // This could be user to read address data..
        /*
        String street = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
        String city = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
        String state = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
        String postalCode = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
        */

        getPhoneNumbers(contentResolver, contactID);

        getEmails(contentResolver, contactID);

        vCardInformation = VCardFormatTool.getFormatedVCardString(name, mobilenumber, homenumber, email);

        mainActivity.setVCardInformationOnMainScreen(vCardInformation);

        contactListDialog.cancel();

        Voice.speakOut(name + "wurde ausgewählt!");

        Vibration.vibrate();
    }

    private void getEmails(ContentResolver contentResolver, String contactID) {

        Cursor emails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactID, null, null);

        if (emails != null) {
            while (emails.moveToNext()) {
                email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            }
        }

        emails.close();
    }

    private void getPhoneNumbers(ContentResolver contentResolver, String contactID) {

        if (Integer.parseInt(mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

            phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactID}, null);

            if (phoneCursor != null && phoneCursor.getCount() > 0) {
                while (phoneCursor.moveToNext()) {
                    String phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    int type = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    // http://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Phone.html
                    // TYPE values are static int so easy to switch.
                    switch (type) {
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
                phoneCursor.close();
            }
        }
    }

    private void displayContacts(MainActivity mainActivity) {
        ContentResolver contentResolver = mainActivity.getContentResolver();
        mainCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (mainCursor != null) {

            if (mainCursor.getCount() > 0) {

                while (mainCursor.moveToNext()) {
                    String name = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    listItems.add(name);
                }
            }
        }
    }

    public void showDialog(){
        contactListDialog.show();
        displayContacts(mainActivity);
    }
}
