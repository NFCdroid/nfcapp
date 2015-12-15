package com.ag.mk.nfccardreadwrite.tools;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import com.ag.mk.nfccardreadwrite.activity.MainActivity;
import com.ag.mk.nfccardreadwrite.addons.Voice;

import java.util.ArrayList;


public class ContactWrite {

    public static void writecontact(final Context context, final ArrayList<String> cardContent){
        //TODO: Implement Contact EDIT
        if (cardContent!=null) {
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

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
