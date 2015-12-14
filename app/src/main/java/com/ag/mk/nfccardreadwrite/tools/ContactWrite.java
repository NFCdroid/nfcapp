package com.ag.mk.nfccardreadwrite.tools;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.ag.mk.nfccardreadwrite.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ContactWrite {

    public static void writecontact(final Context context, final ArrayList<String> cardContent){

        //TODO: Implement Contact EDIT

        if (cardContent!=null) {
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

            intent.putExtra(ContactsContract.Intents.Insert.NAME, cardContent.get(0));
            Log.d("writeContact", "name read.");
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, cardContent.get(1));
            intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            Log.d("writeContact", "mobile read.");
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, cardContent.get(2));
            intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
            Log.d("writeContact", "home read.");
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, cardContent.get(3));
            Log.d("writeContact", "mail read.");
            intent.putExtra("finishActivityOnSaveCompleted", true);

            context.startActivity(intent);
        }
        else{Log.d("error","cardContent == null!");}
    }
}
