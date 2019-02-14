package com.android.gudana.chat.model;


/**
 * Created by anupamchugh on 09/02/16.
 */
public class Model {


    public static final int TEXT_TYPE=0;
    public static final int IMAGE_TYPE=1;
    public static final int AUDIO_TYPE=2;

    public int type;
    public int data;
    public String text;



    public Model(int type, String text, int data)
    {
        this.type=type;
        this.data=data;
        this.text=text;

    }

}
