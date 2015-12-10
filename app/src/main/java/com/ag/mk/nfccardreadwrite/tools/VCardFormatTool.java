package com.ag.mk.nfccardreadwrite.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by marko on 02.12.15.
 */
public class VCardFormatTool {

    private static final String NAME = "N:";
    private static final String MOBILEPHONE = "TEL;TYPE=WORK,VOICE:";
    private static final String HOMEPHONE = "TEL;TYPE=HOME,VOICE:";
    private static final String EMAIL = "EMAIL;TYPE=PREF,INTERNET:";

    public static String[] extractCardInformation(String[] cardContent){

        List<String> tempList = new ArrayList<String>();

        for(int i=0; i<cardContent.length; i++){

            if(cardContent[i].startsWith(NAME)){

                tempList.add("Name: " + cardContent[i].split(NAME)[1].replace(";"," "));

            }else if(cardContent[i].startsWith(MOBILEPHONE)){

                tempList.add("Telefon-Mobil: " + cardContent[i].split(MOBILEPHONE)[1]);

            }else if(cardContent[i].startsWith(HOMEPHONE)){

                tempList.add("Telefon-Festnetz: " + cardContent[i].split(HOMEPHONE)[1]);

            }else if(cardContent[i].startsWith(EMAIL)){

                tempList.add("E-Mail: " + cardContent[i].split(EMAIL)[1]);
            }

        }

        if(tempList.size() > 0) { // TODO noch einen Standart Überlegen
            String[] extractedCardContent = new String[tempList.size()];

            for(int i=0;i<tempList.size();i++){
                extractedCardContent[i] = tempList.get(i);
            }
            return extractedCardContent;
        }
        return  null;
    }

    public static String getFormatedVCardString(String userName, String mobileNumber, String homeNumber, String eMail){

        String formatedVCardString =
                "BEGIN:vcard\r\n"
                        + "VERSION:3.0\r\n"
                        + "N:" + userName.replace(" ",";") + "\r\n"
                        + "TEL;TYPE=WORK,VOICE:" + mobileNumber + "\r\n"
                        + "TEL;TYPE=HOME,VOICE:" + homeNumber + "\r\n"
                        + "EMAIL;TYPE=PREF,INTERNET:" + eMail + "\r\n"
                        + "REV:" + VCardFormatTool.getTimeStamp() + "\r\n"
                        + "END:vcard\r\n";

        return formatedVCardString;
    }

    private static String getTimeStamp(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStamp = (dateFormat.format(new Date()) + "Z").replace(" ", "T"); //REV:2014-03-01T22:11:10Z

        return timeStamp;
    }


}