package com.example.dee_kay.myapplication.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.google.firebase.analytics.FirebaseAnalytics.Event.LOGIN;

/**
 *
 * Handling sql lite database
 * Created by DEE-KAY on 29 Jul 2017.
 */

public class DateBaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "LOGIN.db";

    public static final String TABLE_NAME = "users_table";
    public static final String UserID = "ID";
    public static final String FirstName = "NAME";
    public static final String LastName = "SURNAME";
    public static final String EmailAddress = "EMAIL";
    public static final String Password = "PASSWORD";




    public DateBaseHelper(Context context) {
        super(context, LOGIN, null, 1);
        SQLiteDatabase db = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT,SURNAME TEXT,EMAIL TEXT,PASSWORD TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Insert data into a local db
     * @param name
     * @param surname
     * @param email
     * @param password
     * @return
     */
    public boolean insertData(String name,String surname, String email, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FirstName,name);
        contentValues.put(LastName,surname);
        contentValues.put(EmailAddress,email);
        contentValues.put(Password,password);

      long result =  db.insert(TABLE_NAME,null,contentValues);
        if(result == -1)
        {
            return false;
        }else
        {
            return true;
        }
    }

    /**
     * Getting data from a local db
     * @return
     */
    public Cursor getAllData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;

    }

    public void deleteALL()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME,null,null);
        db.close();
            //db.execSQL("vacuum");
    }

}
