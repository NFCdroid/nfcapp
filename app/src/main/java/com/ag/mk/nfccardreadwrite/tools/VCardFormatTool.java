package com.ag.mk.nfccardreadwrite.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class VCardFormatTool {

    private static final String NAME = "N:";
    private static final String MOBILENUMBER = "TEL;TYPE=WORK,VOICE:";
    private static final String HOMENUMBER = "TEL;TYPE=HOME,VOICE:";
    private static final String EMAIL = "EMAIL;TYPE=PREF,INTERNET:";

    public static ArrayList<String> extractCardInformation(String[] cardContent){

        ArrayList<String> tempList = new ArrayList<String>();

        for(int i=0; i<cardContent.length; i++){
            if(cardContent[i].startsWith(NAME)){

                tempList.add(0, "Name: " + getSplitResult(cardContent[i], NAME).replace(";", " "));

            }else if(cardContent[i].startsWith(MOBILENUMBER)){

                tempList.add(1, "Telefon-Mobil: " + getSplitResult(cardContent[i],MOBILENUMBER));

            }else if(cardContent[i].startsWith(HOMENUMBER)){

                tempList.add(2, "Telefon-Festnetz: " + getSplitResult(cardContent[i], HOMENUMBER));

            }else if(cardContent[i].startsWith(EMAIL)){

                tempList.add(3, "E-Mail: " + getSplitResult(cardContent[i], EMAIL));
            }

        }

        /*if(tempList.size() > 0) { // TODO noch einen Standart Überlegen
            String[] extractedCardContent = new String[tempList.size()];

            for(int i=0;i<tempList.size();i++){
                extractedCardContent[i] = tempList.get(i);
            }
            return extractedCardContent;
        }*/
        return  tempList;
    }

    // Um Ein ArrayIndexOutofBound bei leeren Feldern zu vermeiden
    private static String getSplitResult(String splitme, String Splitparam){
        if (splitme.split(Splitparam).length > 1) {
            return splitme.split(Splitparam)[1];
        }
        else {
            return "";
        }
    }

    public static String getFormatedVCardString(String userName, String mobileNumber, String homeNumber, String eMail){

        return "BEGIN:vcard\r\n"
                + "VERSION:3.0\r\n"
                + "N:" + userName.replace(" ",";") + "\r\n"
                + "TEL;TYPE=WORK,VOICE:" + mobileNumber + "\r\n"
                + "TEL;TYPE=HOME,VOICE:" + homeNumber + "\r\n"
                + "EMAIL;TYPE=PREF,INTERNET:" + eMail + "\r\n"
                + "REV:" + VCardFormatTool.getTimeStamp() + "\r\n"
                + "END:vcard\r\n";
    }

    private static String getTimeStamp(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return (dateFormat.format(new Date()) + "Z").replace(" ", "T");//REV:2014-03-01T22:11:10Z
    }
}
