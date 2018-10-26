package com.android.gudana.group_chat.utils;

import java.text.DateFormat;
import java.util.Date;

import static java.text.DateFormat.getDateTimeInstance;

public class TimeSpanConverter {

    public static Date getTimeDate(long timeStamp){

        try{
            DateFormat dateFormat = getDateTimeInstance();
            Date netDate = (new Date(timeStamp));
            return netDate;
        } catch(Exception e) {
            return null;
        }
    }
}
