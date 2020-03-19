package com.tinybox.safehold.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.tinybox.safehold.constants.DatabaseConstants;

import java.util.ArrayList;
import java.util.List;

public class EmergencyContactDataHandler extends SQLiteOpenHelper {
    public EmergencyContactDataHandler(@Nullable Context context) {
        super(context,  DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);
    }

    public EmergencyContactDataHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL Command
        /**
         * create table_name(id, name, phone_number)
         */
        String CREATE_CONTACT_TABLE = "CREATE TABLE " + DatabaseConstants.TABLE_NAME + "("
                + DatabaseConstants.KEY_CONTACT_ID + " INTEGER PRIMARY KEY )";

        db.execSQL(CREATE_CONTACT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // Add contact
    public void addContactWithID(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // using the ContentValues class to create the values for tables
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.KEY_CONTACT_ID, id);
        // Insert to row
        db.insertOrThrow(DatabaseConstants.TABLE_NAME, null, values);
        db.close(); // closing db connection
    }

    // Add contact
    public void deleteContactWithID(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DatabaseConstants.TABLE_NAME, DatabaseConstants.KEY_CONTACT_ID + "=?", new String[] {String.valueOf(id)});
        db.close();
    }


    public List<Long> getContactIDs(){
        List<Long> contactIds = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectAll = "SELECT * FROM " + DatabaseConstants.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAll, null);

        // Loop through our data
        if (cursor.moveToFirst()) {
            do {
                contactIds.add(Long.parseLong(cursor.getString(0)));
            } while (cursor.moveToNext());
        }

        return contactIds;
    }


}
