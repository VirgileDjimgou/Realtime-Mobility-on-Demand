package com.android.gudana.group_chat.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class OpenNavi {


    public static void Open_map(Context context  , String message){

        String[] LatLong = message.split(":");
        System.out.println(LatLong[0]);
        System.out.println(LatLong[1]);


        // Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194");
        Uri gmmIntentUri = Uri.parse("geo:"+Double.parseDouble(LatLong[0])+","+Double.parseDouble(LatLong[1]));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }


    }


    public static  void Open_navi(Context context  , String message){

        String[] LatLong = message.split(":");
        System.out.println(LatLong[0]); // latiudute
        System.out.println(LatLong[1]); // longitude

        Uri gmmIntentUri = Uri.parse("google.navigation:q="+Double.parseDouble(LatLong[0])+","+Double.parseDouble(LatLong[1]));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }

    }

    public  static  void Open_Street_View(Context context , String message){

        String[] LatLong = message.split(":");
        System.out.println(LatLong[0]);
        System.out.println(LatLong[1]);

        // Uses a PanoID to show an image from Maroubra beach in Sydney, Australia
        Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+Double.parseDouble(LatLong[0])+","+Double.parseDouble(LatLong[1])+"&cbp=0,30,0,0,-15");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }



    }
}
