package com.ag.mk.nfccardreadwrite.addons;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse beinhaltet die Methoden zum Lesen und Schreiben von Dateien für die Anwendung.
 *
 * @author Marko Klepatz
 */
public class DataWork {

    private static Context context;

    public DataWork(Context context){
        DataWork.context = context;
    }

    /**
     * Diese Methode liest eine Datei mit einer Zeile aus und gibt den Inhalt on Form eines Strings zurück.
     *
     * @param dataName übergibt den Namen der zu lesenden Datei
     * @return gibt den ausgelesenen String zurück
     */
    public static String readSingleLineFile(String dataName) {

        BufferedReader read = null;
        String value = null;
        try {
            read = new BufferedReader(new InputStreamReader(context.openFileInput(
                    dataName+".txt")));
            String line;
           // StringBuffer buffer = new StringBuffer();
            while((line=read.readLine()) !=null){

                //buffer.append(line);
                value = line;
            }

        } catch (Exception e) {
            Log.e("ERROR", "File Error, may be not found!");
           // e.printStackTrace();
        }
        finally{
            if(read != null){
                try {
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    /**
     * Diese Methode liest eine Datei mit mehreren Zeilen aus
     * und gibt den Inhalt on Form einer Liste vom Typ String zurück.
     *
     * @param dataName übergibt den Namen der zu lesenden Datei
     * @return values gibt die Liste mit dem Inhalt jeder Zeile zurück
     */
    public static List<String> readMultiLineFile(String dataName) {

        BufferedReader read = null;
        List<String> values = new ArrayList<String>();

        try {
            read = new BufferedReader(new InputStreamReader(context.openFileInput(
                    dataName + ".txt")));
            String zeile;
           // StringBuffer buffer = new StringBuffer();
            while((zeile=read.readLine()) !=null){

              //  buffer.append(zeile);
                values.add(zeile);
            }

        } catch (Exception e) {
            Log.e("ERROR", "File Error, may be not found!");
            //e.printStackTrace();
        }
        finally{
            if(read != null){
                try {
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return values;
    }

    /**
     * Diese Methode schreibt eine einzeilige Information in eine Datei ohne Umbrüche
     *
     * @param dataName übergibt den Namen der zu schreibenden Datei
     * @param information übergibt die zu schreibende Information
     */
    public static void writeSingleLineFile(String dataName, String information) {

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(
                    dataName + ".txt", Context.MODE_PRIVATE)));

            writer.write(information);

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Diese Methode schreibt eine mehrzeilige Information in eine Datei mit Umbrüchen
     *
     * @param dataName übergibt den Namen der zu schreibenden Datei
     * @param informationList übergibt die zu schreibenden Informationen
     */
    public static void writeMultiLineFile(String dataName, List<String> informationList) {

        BufferedWriter writer = null;

        try {

            writer = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(
                    dataName + ".txt", Context.MODE_PRIVATE)));

            for (int i=0;i<informationList.size();i++) {
                writer.write(informationList.get(i));
                writer.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
