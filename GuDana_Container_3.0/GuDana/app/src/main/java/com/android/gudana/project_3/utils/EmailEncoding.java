package com.android.gudana.project_3.utils;

public class EmailEncoding {

    public static String commaEncodePeriod(String email) {
        return email.replace(".", ",");
    }

    public static String commaDecodePeriod(String email) {
        return email.replace(",", ".");
    }
}