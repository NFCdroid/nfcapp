package com.ag.mk.nfccardreadwrite.addressbookwork;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * Diese Klasse beinhaltet die Methode zum Schreiben von Address-Daten in das Adressbuch.
 *
 * @author Klaus Steinhauer, Marko Klepatz
 */
public class AddressBookWriter {

    /**
     * Diese Methode generiert einen Intent, welcher alle zu schreibenden Address-Daten enthält
     * und ruft im Anschluss das Adressbuch über diesen Intent auf und übergibt die Daten.
     *
     * @param context übergibt den Context der rufenden Activity
     * @param cardContent übergibt die zu schreibenden Daten in Form einer Array List
     */
    public static void writeContact(final Context context, final ArrayList<String> cardContent){

        if (cardContent!=null) {
            Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
            intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);

            intent.putExtra(ContactsContract.Intents.Insert.NAME, cardContent.get(0).replace("Name: ", ""));
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, cardContent.get(1).replace("Telefon-Mobil: ", ""));
            intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, cardContent.get(2).replace("Telefon-Festnetz: ", ""));
            intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, cardContent.get(3).replace("E-Mail: ", ""));
            intent.putExtra("finishActivityOnSaveCompleted", true);
            context.startActivity(intent);
        }
    }
}
