package com.ag.mk.nfccardreadwrite.tools;

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
 * Created by Marko on 18.09.2015.
 */
public class DataWork {

    private static Context context;

    public DataWork(Context context){
        this.context = context;
    }

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
