package com.android.gudana.hify.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.gudana.MoD.LiveTrackingObjet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class live_location_sharing_db extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "live_location_sharing_db.db";
    public static final String CONTACTS_TABLE_MATRITCULE_LIVE = "MATRICULE";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_START_TIME = "STARTED_AT";
    public static final String CONTACTS_COLUMN_SHARING_TYPE = "SHARING_TYPE";
    public static final String CONTACTS_COLUMN_MESSAGE = "MESSAGE";
    public static final String CONTACTS_COLUMN_ID_USER = "ID_USER";
    public static final String CONTACTS_COLUMN_ACTIV= "STATUS";


    public static final String Table_live_location = "live_location";
    private HashMap hp;

    public live_location_sharing_db(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table live_location " + "(id integer primary key AUTOINCREMENT,MATRICULE text, STARTED_AT text,SHARING_TYPE text,MESSAGE text,ID_USER text,STATUS text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public void insert_live_location(LiveTrackingObjet LiveLocation) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.CONTACTS_TABLE_MATRITCULE_LIVE, LiveLocation.getCONTACTS_TABLE_MATRITCULE_LIVE());
        contentValues.put(this.CONTACTS_COLUMN_START_TIME, LiveLocation.getCONTACTS_COLUMN_START_TIME());
        contentValues.put(this.CONTACTS_COLUMN_SHARING_TYPE, LiveLocation.getCONTACTS_COLUMN_SHARING_TYPE());
        contentValues.put(this.CONTACTS_COLUMN_MESSAGE, LiveLocation.getCONTACTS_COLUMN_MESSAGE());
        contentValues.put(this.CONTACTS_COLUMN_ID_USER, LiveLocation.getCONTACTS_COLUMN_ID_USER());
        contentValues.put(this.CONTACTS_COLUMN_ACTIV, LiveLocation.getCONTACTS_COLUMN_ACTIV() );
        db.insert(this.Table_live_location, null, contentValues);
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from live_location where id=" + id + "", null);
    }

    public int getRownumber(Cursor rs , String ColumName){
        int colIndex = rs.getColumnIndexOrThrow(ColumName); // = 0
        if (rs.moveToFirst()) {
            return rs.getInt(colIndex);
        } else {
            return  0;
        }
    }


    public List<LiveTrackingObjet> getAllLocationEvent(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + this.Table_live_location, null);

        List<LiveTrackingObjet> fileAllLocation = new ArrayList<>();
        if (cursor.moveToFirst()){
            LiveTrackingObjet LiveLoc = new LiveTrackingObjet(
                    cursor.getString(cursor.getColumnIndex(this.CONTACTS_TABLE_MATRITCULE_LIVE)),
                    cursor.getString(cursor.getColumnIndex(this.CONTACTS_COLUMN_START_TIME)),
                    cursor.getString(cursor.getColumnIndex(this.CONTACTS_COLUMN_SHARING_TYPE)),
                    cursor.getString(cursor.getColumnIndex(this.CONTACTS_COLUMN_MESSAGE)),
                    cursor.getString(cursor.getColumnIndex(this.CONTACTS_COLUMN_ID_USER)),
                    cursor.getString(cursor.getColumnIndex(this.CONTACTS_COLUMN_ACTIV))

            );
            fileAllLocation.add(LiveLoc);
            while(cursor.moveToNext()){

                LiveTrackingObjet LiveLoc_2 = new LiveTrackingObjet(
                        cursor.getString(cursor.getColumnIndex(this.CONTACTS_TABLE_MATRITCULE_LIVE)),
                        cursor.getString(cursor.getColumnIndex(this.CONTACTS_COLUMN_START_TIME)),
                        cursor.getString(cursor.getColumnIndex(this.CONTACTS_COLUMN_SHARING_TYPE)),
                        cursor.getString(cursor.getColumnIndex(this.CONTACTS_COLUMN_MESSAGE)),
                        cursor.getString(cursor.getColumnIndex(this.CONTACTS_COLUMN_ID_USER)),
                        cursor.getString(cursor.getColumnIndex(this.CONTACTS_COLUMN_ACTIV))

                );
                fileAllLocation.add(LiveLoc);
            }
        }
        cursor.close();
        db.close();

        return  fileAllLocation;
    }

    public int getProfilesCount() {
        String countQuery = "SELECT  * FROM " + this.Table_live_location;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }


    public void update_location_activ(Integer id, String CONTACTS_COLUMN_ACTIV) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.CONTACTS_COLUMN_ACTIV, CONTACTS_COLUMN_ACTIV);
        db.update(this.Table_live_location, contentValues, "id = ? ", new String[]{Integer.toString(id)});
    }



    public Integer deleteLive_location(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }
}

