package com.ag.mk.nfccardreadwrite.tools;

import android.nfc.NfcAdapter;
import android.util.Log;
import android.widget.Toast;

import com.ag.mk.nfccardreadwrite.activity.MainActivity;
import com.ag.mk.nfccardreadwrite.addons.Voice;

/**
 * Diese Klasse beinhaltet die Methoden zum Überprüfen ob die NFC Technologie vorhanden und aktiviert ist.
 *
 * @author Marko Klepatz
 */
public class NfcTools {

    private MainActivity mainActivity;
    private NfcAdapter nfcAdapter;

    public NfcTools(MainActivity mainActivity, NfcAdapter nfcAdapter){
        this.mainActivity = mainActivity;
        this.nfcAdapter = nfcAdapter;
    }


    /**
     * Diese Methode überprüft ob die NFC Technologie aktiviert ist
     * und gibt eine Warnung in Form einer Toast Nachricht aus wenn sie deaktiviert ist.
     */
    public void checkNFC() {
        if (nfcAdapter.isEnabled()) {
            Log.i(MainActivity.TAG, "NFC is enabled.");
        } else {
            Log.i(MainActivity.TAG, "NFC is disabled.");
            Toast.makeText(mainActivity, "NFC ist deaktiviert!", Toast.LENGTH_LONG).show();
            Voice.speakOut("NFC ist deaktiviert!");
        }
    }

    /**
     * Diese Methode überprüft ob die NFC Technologie auf dem Gerät vorhanden ist
     * und gibt eine Toast Nachricht aus wenn sie es nicht ist und beendet die App.
     */
    public void checkNFCSupport() {
        if (nfcAdapter == null) {
            Toast.makeText(mainActivity, "Dieses Gerät unterstützt kein NFC!", Toast.LENGTH_LONG).show();
            Voice.speakOut("Dieses Gerät unterstützt kein NFC!");
            mainActivity.finish();
        }
    }
}
