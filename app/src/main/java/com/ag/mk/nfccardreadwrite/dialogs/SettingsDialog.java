package com.ag.mk.nfccardreadwrite.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.ag.mk.nfccardreadwrite.R;
import com.ag.mk.nfccardreadwrite.activity.MainActivity;
import com.ag.mk.nfccardreadwrite.addons.Vibration;
import com.ag.mk.nfccardreadwrite.addons.Voice;
import com.ag.mk.nfccardreadwrite.tools.DataWork;

/**
 * Diese Klasse beinhaltet alle Methoden zum Generieren und Anzeigen des Einstellungs-Dialogs.
 * Weiterführend werden hier auch alle Einstellungen direkt in Dateien auf dem Handy geschrieben.
 *
 * @see DataWork
 *
 * @author Marko Klepatz
 */
public class SettingsDialog {

    private Button backButton;

    private Switch voiceSwitch, vibrationSwitch;

    private Dialog settingsDialog;

    private MainActivity mainActivity;

    /**
     * Dieser Konstruktor leitet alle Initialisierungen für den SettingsDialog ein.
     *
     * @param mainActivity übergibt die Klasse MainActivity für den Context der GUI Elemente
     *
     * @see MainActivity
     */
    public SettingsDialog(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        initContactDialog();
    }

    /**
     * Diese Methode initialisiert den Dialog und leitet die Initialisierung aller GUI Objekte ein.
     */
    private void initContactDialog(){

        settingsDialog = new Dialog(mainActivity);

        LayoutInflater inflater = (LayoutInflater)mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_settings, (ViewGroup)mainActivity.findViewById(R.id.contactList));

        settingsDialog.setContentView(layout);
        settingsDialog.setTitle("Einstellungen");

        initButtons(layout);

        initSwitches(layout);
    }


    /**
     * Diese Methode initialisiert alle Button Objekte und deren Logik.
     */
    private void initButtons(View layout) {
        backButton = (Button) layout.findViewById(R.id.settingsBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibration.vibrate();
                settingsDialog.cancel();
            }
        });
    }

    /**
     * Diese Methode initialisiert alle Switch Objekte und deren Logik.
     *
     * @see DataWork
     */
    private void initSwitches(View layout){

        vibrationSwitch = (Switch) layout.findViewById(R.id.vibrationSwitch);
        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Vibration.setVibration(true);
                    Voice.speakOut("Vibration aktiviert.");
                } else {
                    Vibration.setVibration(false);
                    Voice.speakOut("Vibration deaktiviert.");
                }

                DataWork.writeSingleLineFile("vibration", isChecked + "");
            }
        });

        voiceSwitch = (Switch) layout.findViewById(R.id.voiceSwitch);
        voiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                System.out.println("VOICE: " + isChecked);

                if (isChecked) {
                    Voice.setSound(true);
                } else {
                    Voice.setSound(false);
                }

                DataWork.writeSingleLineFile("voice", isChecked + "");
            }
        });
    }

    public void showDialog(){
        settingsDialog.show();
    }

    public Switch getVibrationSwitch() {
        return vibrationSwitch;
    }

    public Switch getVoiceSwitch() {
        return voiceSwitch;
    }
}
