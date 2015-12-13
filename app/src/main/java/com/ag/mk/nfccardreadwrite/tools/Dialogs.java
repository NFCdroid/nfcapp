package com.ag.mk.nfccardreadwrite.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.ag.mk.nfccardreadwrite.activity.ContactsActivity;
import com.ag.mk.nfccardreadwrite.activity.CreateVCardActivity;
import com.ag.mk.nfccardreadwrite.cardwork.CardWriter;

/**
 * Created by marko on 13.12.15.
 */
public class Dialogs{

    public Dialogs(){

    }

    public void showBeamOrWriteDialog(final ContactsActivity contactsActivity, final String vCardInformation){

        AlertDialog beamOrWriteDialog;

        AlertDialog.Builder shutDownDialogBuilder = new AlertDialog.Builder(contactsActivity);


        shutDownDialogBuilder.setMessage("Bitte wählen Sie eine Übertragungsart!")
                .setTitle("Übertragungsart");

        shutDownDialogBuilder.setPositiveButton("Android-Beam", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                contactsActivity.startBeamMode();
                Vibration.vibrate();
            }
        });
        shutDownDialogBuilder.setNegativeButton("V-Card", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(contactsActivity, CreateVCardActivity.class);
                intent.setAction(AppWidgetManager.EXTRA_CUSTOM_EXTRAS);
                intent.putExtra("vcardinformation", vCardInformation);
                contactsActivity.startActivity(intent);
                Vibration.vibrate();
            }
        });

        beamOrWriteDialog = shutDownDialogBuilder.create();

        beamOrWriteDialog.show();
    }

}
