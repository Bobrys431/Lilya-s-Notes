package com.example.lilyasnotes.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.NoSuchElementException;

public class ThemeIntoManager {

    private static SQLiteDatabase database;

    public static void build(Context context) {
        database = SQLiteDatabaseAdapter.getDatabase(context);
    }

    public static int getThemeIndex(int id) {
        Cursor cursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_INTO_INDEX +
                " FROM " + SQLiteDatabaseAdapter.THEME_INTO +
                " WHERE " + SQLiteDatabaseAdapter.THEME_INTO_IN_ID + " = " + id, null);

        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_INTO_INDEX));
            cursor.close();
            return index;
        }
        throw new NoSuchElementException("Missing Theme with id " + id);
    }

    public static int getThemeId(int id, int index) {
        Cursor cursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_INTO_IN_ID +
                " FROM " + SQLiteDatabaseAdapter.THEME_INTO +
                " WHERE " + SQLiteDatabaseAdapter.THEME_INTO_INDEX + " = " + index +
                " AND " + SQLiteDatabaseAdapter.THEME_INTO_THEME_ID + " = " + id, null);

        if (cursor != null && cursor.moveToFirst()) {
            int inId = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_INTO_IN_ID));
            cursor.close();
            return inId;
        }
        throw new NoSuchElementException("Missing Theme with id " + id + " or with index " + index);
    }

    public static void addConnection(int id, int inId) {
        database.execSQL("INSERT INTO " + SQLiteDatabaseAdapter.THEME_INTO +
                "(" + SQLiteDatabaseAdapter.THEME_INTO_THEME_ID +
                ", " + SQLiteDatabaseAdapter.THEME_INTO_INDEX +
                ", " + SQLiteDatabaseAdapter.THEME_INTO_IN_ID +")" +
                " VALUES (" + id +
                ", " + generateNewIndex(id) +
                ", " + inId + ")");
    }

    private static int generateNewIndex(int id) {
        Cursor lastThemeIndex = database.rawQuery("SELECT COUNT(" + SQLiteDatabaseAdapter.THEME_INTO_ID + ") AS max_index" +
                " FROM " + SQLiteDatabaseAdapter.THEME_INTO +
                " WHERE " + SQLiteDatabaseAdapter.THEME_INTO_THEME_ID + " = " + id, null);

        Cursor lastNoteIndex = database.rawQuery("SELECT COUNT(" + SQLiteDatabaseAdapter.THEME_NOTE_ID + ") AS max_index" +
                " FROM " + SQLiteDatabaseAdapter.THEME_NOTE +
                " WHERE " + SQLiteDatabaseAdapter.THEME_NOTE_THEME_ID + " = " + id, null);

        int index = 0;

        if (lastThemeIndex != null && lastThemeIndex.moveToFirst()) {
            index += lastThemeIndex.getInt(lastThemeIndex.getColumnIndexOrThrow("max_index"));
            lastThemeIndex.close();
        }
        if (lastNoteIndex != null && lastNoteIndex.moveToFirst()) {
            index += lastNoteIndex.getInt(lastNoteIndex.getColumnIndexOrThrow("max_index"));
            lastNoteIndex.close();
        }

        return index;
    }

    public static void deleteConnection(int id) {
        int index;
        try {
            index = getThemeIndex(id);
        } catch (NoSuchElementException e) {
            return;
        }

        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.THEME_INTO +
                " WHERE " + SQLiteDatabaseAdapter.THEME_INTO_IN_ID + " = " + id);

        database.execSQL("UPDATE " + SQLiteDatabaseAdapter.THEME_INTO +
                " SET " + SQLiteDatabaseAdapter.THEME_INTO_INDEX + " = " + SQLiteDatabaseAdapter.THEME_INTO_INDEX + " - 1" +
                " WHERE " + SQLiteDatabaseAdapter.THEME_INTO_INDEX + " > " + index);
    }

    public static void translateThemeUp(int id, int inId) {
        int index = getThemeIndex(inId);
        int secondInId;
        boolean isNote = false;
        try {
            secondInId = ThemeNoteManager.getNoteId(id, index - 1);
            isNote = true;
        } catch (NoSuchElementException e) {
            secondInId = getThemeId(id, index - 1);
        }

        setThemeIndex(inId, index - 1);
        if (isNote) ThemeNoteManager.setNoteIndex(secondInId, index);
        else setThemeIndex(secondInId, index);
    }

    public static void translateThemeDown(int id, int inId) {
        int index = getThemeIndex(inId);
        int secondInId;
        boolean isNote = false;
        try {
            secondInId = ThemeNoteManager.getNoteId(id, index + 1);
            isNote = true;
        } catch (NoSuchElementException e) {
            secondInId = getThemeId(id, index + 1);
        }

        setThemeIndex(inId, index + 1);
        if (isNote) ThemeNoteManager.setNoteIndex(secondInId, index);
        else setThemeIndex(secondInId, index);
    }

    public static void setThemeIndex(int id, int index) {
        database.execSQL("UPDATE " + SQLiteDatabaseAdapter.THEME_INTO +
                " SET " + SQLiteDatabaseAdapter.THEME_INTO_INDEX + " = " + index +
                " WHERE " + SQLiteDatabaseAdapter.THEME_INTO_IN_ID + " = " + id);
    }
}
