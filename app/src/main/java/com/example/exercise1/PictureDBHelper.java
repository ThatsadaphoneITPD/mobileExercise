package com.example.exercise1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class PictureDBHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "Pic.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PIC = "Pic";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_IMAGE = "img";

    public PictureDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = " CREATE TABLE " + TABLE_PIC +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_IMAGE + " TEXT NOT NULL) ;";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PIC);
        onCreate(db);
    }
    //Add URL array to DB
    public void addURL(String Url ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues dbImg = new ContentValues();
        dbImg.put(COLUMN_IMAGE, Url);
        long result = db.insert(TABLE_PIC, null, dbImg);
        if(result == -1){
            Toast.makeText(context, "Failed Insert Image URL", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully! new image URL", Toast.LENGTH_SHORT).show();
        }
    }

    //Delete All Trip
    void deleteImages(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM"+ " " + TABLE_PIC);
    }
    //Display all Data from SQLife table to MainActivity
    Cursor readAllData(){
        //select my_trip table to return new Cursor Object to Query
        String query = "SELECT * FROM " + TABLE_PIC;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    };
}
