package com.ag.mk.nfccardreadwrite.tools;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.provider.Settings;
import android.widget.Toast;

import com.ag.mk.nfccardreadwrite.activity.MainActivity;
import com.ag.mk.nfccardreadwrite.addons.Voice;

/**
 * Diese Klasse beinhaltet die Methode zum einleiten des Beamvorgangs.
 *
 * @author Oliver Friedrich
 */
public class BeamTools {
    private MainActivity mainActivity;
    public BeamTools(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    /**
     * Diese Methode leitet den Beamvorgang ein.
     * @param nfcAdapter übergibt die NFC-Schnittstelle und versetzt diese in den Beam-Modus.
     */
    public void startBeamMode(NfcAdapter nfcAdapter) {
        // Prüfe ob Beam aktiv ist
        if (nfcAdapter.isNdefPushEnabled()) {
            Toast.makeText(mainActivity, "Beam Modus gestartet...", Toast.LENGTH_SHORT).show();
            Voice.speakOut("Beam Modus gestartet!");
            nfcAdapter.setNdefPushMessageCallback(mainActivity, mainActivity);
        } else {
            Toast.makeText(mainActivity, "Beam nicht aktiviert!\nBitte Beam in den Settings aktivieren.", Toast.LENGTH_SHORT).show();
            Voice.speakOut("Beam nicht aktiviert!\nBitte Beam in den Settings aktivieren.");

            mainActivity.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            mainActivity.finish();
        }
    }
}
