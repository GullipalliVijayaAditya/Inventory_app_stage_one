package com.example.android.inventory_app_stage_one.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class ProductProvider extends ContentProvider {
    public static final int PRODUCTS = 200;
    public static final int PRODUCT_ID = 201;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    DbHelper mDBHelper;

    @Override
    public boolean onCreate() {
        mDBHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case PRODUCTS:
                cursor = db.query(Contract.NewEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = Contract.NewEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(Contract.NewEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case PRODUCTS:
                return Contract.NewEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return Contract.NewEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int numRowsDeleted;
        switch (sUriMatcher.match(uri)) {
            case PRODUCTS:
                numRowsDeleted = db.delete(Contract.NewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = Contract.NewEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                numRowsDeleted = db.delete(Contract.NewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                selection = Contract.NewEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update not supported");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalStateException("Insertion is not supported");
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        if (values.getAsString(Contract.NewEntry.COLUMN_PRODUCT_NAME) == null)
            throw new IllegalArgumentException("Product Requires a name");
        if (values.getAsString(Contract.NewEntry.COLUMN_SUPPLIER_NAME) == null)
            throw new IllegalArgumentException("Product Supplier Requires a name");
        String phoneNumber = values.getAsString(Contract.NewEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (phoneNumber == null || phoneNumber.length() != 10)
            throw new IllegalArgumentException("Invalid Supplier Phone Number");
        Integer quantity = values.getAsInteger(Contract.NewEntry.COLUMN_QUANTITY);
        if (quantity != null && quantity <= 0)
            throw new IllegalArgumentException("Invalid number of products");
        Integer cost = values.getAsInteger(Contract.NewEntry.COLUMN_PRICE);
        if (cost != null && cost <= 0)
            throw new IllegalArgumentException("Invalid cost of product");
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long id = db.insert(Contract.NewEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.v("insertProduct() method", "Insertion error");
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(Contract.NewEntry.COLUMN_PRODUCT_NAME)) {
            if (values.getAsString(Contract.NewEntry.COLUMN_PRODUCT_NAME) == null)
                throw new IllegalArgumentException("Product Requires a name");
        }
        if (values.containsKey(Contract.NewEntry.COLUMN_SUPPLIER_NAME)) {
            if (values.getAsString(Contract.NewEntry.COLUMN_SUPPLIER_NAME) == null)
                throw new IllegalArgumentException("Product Supplier Requires a name");
        }
        if (values.containsKey(Contract.NewEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String phoneNumber = values.getAsString(Contract.NewEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (phoneNumber == null || phoneNumber.length() != 10)
                throw new IllegalArgumentException("Invalid Supplier Phone Number");
        }
        if (values.containsKey(Contract.NewEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(Contract.NewEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity <= 0)
                throw new IllegalArgumentException("Invalid number of products");
        }
        if (values.containsKey(Contract.NewEntry.COLUMN_PRICE)) {
            Integer cost = values.getAsInteger(Contract.NewEntry.COLUMN_PRICE);
            if (cost != null && cost <= 0)
                throw new IllegalArgumentException("Invalid cost of product");
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int rowsChanged = db.update(Contract.NewEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsChanged != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsChanged;
    }
}