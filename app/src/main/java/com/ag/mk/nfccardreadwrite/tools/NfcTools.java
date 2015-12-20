package com.ag.mk.nfccardreadwrite.tools;

import android.nfc.NfcAdapter;
import android.util.Log;
import android.widget.Toast;

import com.ag.mk.nfccardreadwrite.activity.MainActivity;

/**
 * Created by marko on 16.12.15.
 */
public class NfcTools {

    private MainActivity mainActivity;
    private NfcAdapter nfcAdapter;

    public NfcTools(MainActivity mainActivity, NfcAdapter nfcAdapter){
        this.mainActivity = mainActivity;
        this.nfcAdapter = nfcAdapter;
    }


    public void checkNFC() {
        if (nfcAdapter.isEnabled()) {
            Log.i(MainActivity.TAG, "NFC is enabled.");
        } else {
            Log.i(MainActivity.TAG, "NFC is disabled.");
        }
    }

    public void checkNFCSupport() {
        if (nfcAdapter == null) {
            Toast.makeText(mainActivity, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            mainActivity.finish();
        }
    }
}
