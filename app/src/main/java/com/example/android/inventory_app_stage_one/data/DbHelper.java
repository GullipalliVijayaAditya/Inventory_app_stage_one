package com.example.android.inventory_app_stage_one.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "inventory.db";
    public static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + Contract.NewEntry.TABLE_NAME + " (" + Contract.NewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Contract.NewEntry.COLUMN_PRODUCT_NAME + " TEXT, " + Contract.NewEntry.COLUMN_PRICE + " INTEGER, " + Contract.NewEntry.COLUMN_QUANTITY + " INTEGER, " + Contract.NewEntry.COLUMN_SUPPLIER_NAME + " TEXT, " + Contract.NewEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT);";
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}