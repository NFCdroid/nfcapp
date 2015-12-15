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

/**
 * Created by marko on 15.12.15.
 */
public class SettingsDialog {

    private Button backButton;

    private Switch vibrationSwitch, voiceSwitch;

    private Dialog settingsDialog;

    private MainActivity mainActivity;

    public SettingsDialog(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        initContactDialog();
    }

    public void initContactDialog(){

        settingsDialog = new Dialog(mainActivity);

        LayoutInflater inflater = (LayoutInflater)mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_settings, (ViewGroup)mainActivity.findViewById(R.id.contactList));

        settingsDialog.setContentView(layout);
        settingsDialog.setTitle("Einstellungen");

        initButtons(layout);

        initSwitches(layout);
    }


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

    private void initSwitches(View layout){

        vibrationSwitch = (Switch) layout.findViewById(R.id.vibrationSwitch);
        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    Vibration.setVibration(true);
                    Voice.speakOut("Vibration aktiviert.");
                }else{
                    Vibration.setVibration(false);
                    Voice.speakOut("Vibration deaktiviert.");
                }
            }
        });

        voiceSwitch = (Switch) layout.findViewById(R.id.voiceSwitch);
        voiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                System.out.println("VOICE: "+isChecked);

                if(isChecked){
                    Voice.setSound(true);
                }else{
                    Voice.setSound(false);
                }
            }
        });
    }

    public void showDialog(){
        settingsDialog.show();
    }
}
