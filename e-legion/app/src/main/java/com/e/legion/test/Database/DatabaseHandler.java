package com.e.legion.test.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseHandler extends SQLiteAssetHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "quiz.db";
    private SQLiteDatabase mDbRead = this.getReadableDatabase();
    private SQLiteDatabase mDbWrite = this.getWritableDatabase();

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor readTable(String tableName) {
            return mDbRead.query(tableName, null, null, null, null, null, null);
    }

    public Cursor readTableQuery(String tableName, String[] columns,
                                 String selection, String[] selectionArgs,
                                 String groupBy, String having, String orderBy) {

        return mDbRead.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public void insertToTable(String tableName, ContentValues newData) {
        mDbWrite.insert(tableName, null, newData);
    }

    public void updateTable(String tableName, ContentValues newData, String whereClause, int itemID) {
        mDbWrite.update(tableName, newData, whereClause + " = ?", new String[]{itemID + ""});
    }

    public void deleteFromTable(String tableName, String whereClause, int itemID) {
        mDbWrite.delete(tableName, whereClause + " = ?", new String[]{itemID + ""});
    }

    public Cursor orderBy(String tableName, String orderBy) {
        return mDbRead.query(tableName, null, null, null, null, null, orderBy);
    }

    public Cursor orderByDESC(String tableName, String columnName) {
        String sql = "SELECT * FROM " + tableName + " ORDER BY " + columnName + " DESC";
        return mDbRead.rawQuery(sql, null);
    }

    public boolean isTableIEmpty(String tableName) {
        return !mDbRead.query(tableName, null, null, null, null, null, null).moveToFirst();
    }

    public String getTableAsString(String tableName) {
        String tableString = String.format("Table %s:\n", tableName);
        Cursor cursor = mDbRead.rawQuery("SELECT * FROM " + tableName, null);

        if (cursor.moveToFirst()) {
            String[] columnNames = cursor.getColumnNames();
            do {
                for (String name : columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            cursor.getString(cursor.getColumnIndex(name)));
                }
            } while (cursor.moveToNext());
        }

        return tableString;
    }
}