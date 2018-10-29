package com.android.gudana.group_chat.utils;

public class EmailEncoding {

    public static String commaEncodePeriod(String email) {
        return email.replace(".", ",");
    }

    public static String commaDecodePeriod(String email) {
        return email.replace(",", ".");
    }
}
