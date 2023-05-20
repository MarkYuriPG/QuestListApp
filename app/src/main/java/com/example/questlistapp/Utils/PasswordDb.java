package com.example.questlistapp.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PasswordDb extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "passwords.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase passwordDb;

    public PasswordDb(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase passwordDb) {
        this.passwordDb = passwordDb;
        String createTableQuery = "CREATE TABLE passwords (id INTEGER PRIMARY KEY AUTOINCREMENT, password TEXT)";
        passwordDb.execSQL(createTableQuery);

       /* if(getPassword() == null)
        {
            ContentValues values = new ContentValues();
            values.put("password", "user1234");
            long rowId = passwordDb.insert("passwords", null, values);
        }*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public String getPassword()
    {
        SQLiteDatabase passwordDb = getWritableDatabase();
        String[] projection = {"password"};
        String selection = "id = ?";
        String[] selectionArgs = {"1"}; // Assuming you want to retrieve the password with id = 1
        Cursor cursor = passwordDb.query("passwords", projection, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            // Use the retrieved password
            return password;
        }
        cursor.close();

        return null;
    }

    public void updatePassword(String newPassword){
        SQLiteDatabase passwordDb = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword); // Replace "newPassword" with the new password
        String whereClause = "id = ?";
        String[] whereArgs = {"1"}; // Assuming you want to update the password with id = 1
        int rowsUpdated = passwordDb.update("passwords", values, whereClause, whereArgs);
    }
}
