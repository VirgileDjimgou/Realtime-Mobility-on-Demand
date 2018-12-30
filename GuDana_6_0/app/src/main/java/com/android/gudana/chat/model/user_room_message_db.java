package com.android.gudana.chat.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class user_room_message_db extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "user_room_message_db.db";
    public static final String ROOM_ID = "ROOM_ID";
    public static final String ROOM_UID = "ROOM_UID";
    public static final String USER_ID = "USER_ID";
    public static final String USER_UID = "USER_UID";
    public static final String CONTENT= "CONTENT";
    public static final String DataTime= "DataTime";

    public static final String Table_room_message= "room_message";
    private HashMap hp;

    public user_room_message_db(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table room_message " + "(id integer primary key AUTOINCREMENT,ROOM_ID text, ROOM_UID text,USER_ID text, USER_UID text,CONTENT text,DataTime text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS room_message");
        onCreate(db);
    }

    public void insert_new_message(message message) {
        try{


            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(this.ROOM_ID, message.getROOM_ID());
            contentValues.put(this.ROOM_UID, message.getROOM_UID());
            contentValues.put(this.USER_ID, message.getUSER_ID());
            contentValues.put(this.USER_UID, message.getUSER_UID());
            contentValues.put(this.CONTENT, message.getCONTENT());
            contentValues.put(this.DataTime, message.getDataTime());
            db.insert(this.Table_room_message, null, contentValues);
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public Cursor getData_message(int id) {
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            return db.rawQuery("select * from room_message where id=" + id + "", null);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public int getRownumber(Cursor rs , String ColumName){
        try{

            int colIndex = rs.getColumnIndexOrThrow(ColumName); // = 0
            if (rs.moveToFirst()) {
                return rs.getInt(colIndex);
            } else {
                return  0;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return  0;

    }


    public List<message> getAllMessage(){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + this.Table_room_message, null);

        List<message> fileAllLocation = new ArrayList<>();
        try{

            if (cursor.moveToFirst()){
                message local_message = new message(
                        cursor.getString(cursor.getColumnIndex(this.ROOM_ID)),
                        cursor.getString(cursor.getColumnIndex(this.ROOM_UID)),
                        cursor.getString(cursor.getColumnIndex(this.USER_ID)),
                        cursor.getString(cursor.getColumnIndex(this.USER_UID)),
                        cursor.getString(cursor.getColumnIndex(this.CONTENT)),
                        cursor.getString(cursor.getColumnIndex(this.DataTime))

                );
                fileAllLocation.add(local_message);
                while(cursor.moveToNext()){

                    message second_message = new message(

                            cursor.getString(cursor.getColumnIndex(this.ROOM_ID)),
                            cursor.getString(cursor.getColumnIndex(this.ROOM_UID)),
                            cursor.getString(cursor.getColumnIndex(this.USER_ID)),
                            cursor.getString(cursor.getColumnIndex(this.USER_UID)),
                            cursor.getString(cursor.getColumnIndex(this.CONTENT)),
                            cursor.getString(cursor.getColumnIndex(this.DataTime))

                    );
                    fileAllLocation.add(second_message);

                }
            }
            cursor.close();
            db.close();

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return  fileAllLocation;
    }

    public int getNumberOfmessage() {
        try{


            String countQuery = "SELECT  * FROM " + this.Table_room_message;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            int count = cursor.getCount();
            cursor.close();
            return count;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return 0;

    }



    public Integer delete_message(Integer id) {
        try{


            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete("contacts",
                    "id = ? ",
                    new String[]{Integer.toString(id)});
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}

