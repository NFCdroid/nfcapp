package com.ag.mk.nfccardreadwrite.addressbookwork;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.ag.mk.nfccardreadwrite.activity.MainActivity;
import com.ag.mk.nfccardreadwrite.addons.Vibration;
import com.ag.mk.nfccardreadwrite.addons.Voice;
import com.ag.mk.nfccardreadwrite.dialogs.ContactListDialog;
import com.ag.mk.nfccardreadwrite.tools.VCardFormatTools;

import java.util.ArrayList;

/**
 * Diese Klasse beinhaltet alle Methoden zum Auslesen eines Kontaktes aus dem Adressbuch.
 *
 * @author Klaus Steinhauer, Marko Klepatz
 */
public class AddressBookReader {

    private ArrayList<String> listItems = new ArrayList<>();

    private String vCardInformation, mobilenumber = "---", homenumber = "---", worknumber = "---", email = "---";

    private Cursor mainCursor, phoneCursor;

    private MainActivity mainActivity;

    public AddressBookReader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    /**
     * Diese Methode sammelt beim Aufruf alle benötigten Daten zum ausgewählten Kontakt.
     *
     * @param position übergibt die Position aus der Namen-ListView zum lokalisieren des Kontaktes im Adressbuch
     */
    public void getAdressbookData(int position) {

        ContentResolver contentResolver = mainActivity.getContentResolver();
        mainCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        assert mainCursor != null;
        mainCursor.moveToPosition(position);

        String contactID = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts._ID));
        String name = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

        // Not yet implemented in VCardFormatTools
        // This could be user to read address data..
        /*
        String street = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
        String city = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
        String state = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
        String postalCode = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
        */

        getPhoneNumbers(contentResolver, contactID);

        getEmails(contentResolver, contactID);

        vCardInformation = VCardFormatTools.getFormatedVCardString(name, mobilenumber, homenumber, email);

        mainActivity.setVCardInformationOnMainScreen(vCardInformation);

        Voice.speakOut(name + "wurde ausgewählt!");

        Vibration.softVibrate();
    }

    /**
     * Diese Methode liest die E-Mail Adressen des ausgewählten Kontakts aus.
     *
     * @param contentResolver
     * @param contactID übergibt die ID des ausgewählten Kontakts
     */
    private void getEmails(ContentResolver contentResolver, String contactID) {

        Cursor emails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactID, null, null);

        if (emails != null) {
            while (emails.moveToNext()) {
                email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            }
        }

        if(emails != null)emails.close();
    }

    /**
     * Diese Methode liest die Telefon-Nummern des ausgewählten Kontakts aus.
     *
     * @param contentResolver
     * @param contactID übergibt die ID des ausgewählten Kontakts
     */
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

    /**
     * Diese Methode liest alle Kontakte im Adressbuch aus
     * und schreibt die Namen in die statische <b>listItems</b> Liste aus der <b>ContactListDialog</b> Klasse.
     *
     * @param contentResolver
     *
     * @see ContactListDialog
     */
    public void readAllContacts(ContentResolver contentResolver) {

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

    public ArrayList<String> getListItems() {
        return listItems;
    }
}
