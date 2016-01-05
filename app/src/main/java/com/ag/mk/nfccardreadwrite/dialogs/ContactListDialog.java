package com.ag.mk.nfccardreadwrite.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.ag.mk.nfccardreadwrite.R;
import com.ag.mk.nfccardreadwrite.activity.MainActivity;
import com.ag.mk.nfccardreadwrite.addressbookwork.AddressBookReader;
import com.ag.mk.nfccardreadwrite.addons.Vibration;

import java.util.ArrayList;

/**
 * Diese Klasse beinhaltet die Methoden zum Initialisieren und Anzeigen des Kontakt-Listen-Dialogs.
 *
 * @author Marko Klepatz
 */
public class ContactListDialog {

    private ListView listView;

    private Button backButton;

    private Dialog contactListDialog;
    private AddressBookReader addressBookReader;
    private MainActivity mainActivity;

    /**
     * Dieser Konstruktor leitet alle Initialisierungen für den ContactListDialog ein.
     *
     * @param mainActivity übergibt die Klasse MainActivity für den Context der GUI Elemente
     */
    public ContactListDialog(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        this.addressBookReader = new AddressBookReader(mainActivity);

        initContactDialog();
    }

    /**
     * Diese Methode initialisiert den Dialog und leitet die Initialisierung aller GUI Objekte ein.
     */
    private void initContactDialog(){

        contactListDialog = new Dialog(mainActivity);

        LayoutInflater inflater = (LayoutInflater)mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_contactlist, (ViewGroup)mainActivity.findViewById(R.id.contactList));

        contactListDialog.setContentView(layout);
        contactListDialog.setTitle("Kontakte");

        initButtons(layout);

        initListviews(layout);

    }

    /**
     * Diese Methode initialisiert alle Listview Objekte und deren Logik.
     */
    private void initListviews(View layout) {
        listView = (ListView) layout.findViewById(R.id.contactList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addressBookReader.getAdressbookData(position);
                contactListDialog.cancel();

            }
        });

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_list_item_1, android.R.id.text1, addressBookReader.getListItems());

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
            }
        });
    }

    /**
     * Diese Methode initialisiert alle Buttons Objekte und deren Logik.
     */
    private void initButtons(View layout) {
        backButton = (Button) layout.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibration.vibrate();
                contactListDialog.cancel();
            }
        });
    }

    /**
     * Diese Methode leitet das Auslesen der Kontakte ein und ruft die Anzeigemethode für den ContactListDialog.
     */
    public void showDialog(){
        addressBookReader.readAllContacts(mainActivity.getContentResolver());
        contactListDialog.show();
    }
}
