package com.android.gudana.hify.utils;

import java.util.Random;

public class random_Utils {


    public  static String  getRandom(int min , int max){
        Random r = new Random();
        int low = min;
        int high = max;
        int result = r.nextInt(high-low) + low;

        return  Integer.toString(result);
    }
}
