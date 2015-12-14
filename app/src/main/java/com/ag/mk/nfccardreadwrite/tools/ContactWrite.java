package com.ag.mk.nfccardreadwrite.tools;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;

import com.ag.mk.nfccardreadwrite.activity.MainActivity;

import java.util.List;

/**
 * Created by klaus on 14.12.15.
 */

public class ContactWrite {

    public static void writecontact(final Context context, final List<String> cardContent){

        //TODO: Implement Contact EDIT

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.NAME, cardContent.get(0));
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, cardContent.get(1));
        intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, cardContent.get(2));
        intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, cardContent.get(3));
        intent.putExtra("finishActivityOnSaveCompleted", true);

        context.startActivity(intent);

    }
}
