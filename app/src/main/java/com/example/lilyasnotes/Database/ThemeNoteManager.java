package com.example.lilyasnotes.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.NoSuchElementException;

public class ThemeNoteManager {

    private static SQLiteDatabase database;

    public static void build(Context context) {
        database = SQLiteDatabaseAdapter.getDatabase(context);
    }

    public static int getNoteIndex(int id) {
        Cursor cursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_NOTE_INDEX +
                " FROM " + SQLiteDatabaseAdapter.THEME_NOTE +
                " WHERE " + SQLiteDatabaseAdapter.THEME_NOTE_IN_ID + " = " + id, null);

        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_NOTE_INDEX));
            cursor.close();
            return index;
        }
        throw new NoSuchElementException("Missing Note with id " + id);
    }

    public static int getNoteId(int id, int index) {
        Cursor cursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_NOTE_IN_ID +
                " FROM " + SQLiteDatabaseAdapter.THEME_NOTE +
                " WHERE " + SQLiteDatabaseAdapter.THEME_NOTE_INDEX + " = " + index +
                " AND " + SQLiteDatabaseAdapter.THEME_NOTE_THEME_ID + " = " + id, null);

        if (cursor != null && cursor.moveToFirst()) {
            int inId = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_NOTE_IN_ID));
            cursor.close();
            return inId;
        }
        throw new NoSuchElementException("Missing Theme with id " + id + " or index " + index);
    }

    public static void addConnection(int id, int inId) {
        database.execSQL("INSERT INTO " + SQLiteDatabaseAdapter.THEME_NOTE +
                "(" + SQLiteDatabaseAdapter.THEME_NOTE_THEME_ID +
                ", " + SQLiteDatabaseAdapter.THEME_NOTE_INDEX +
                ", " + SQLiteDatabaseAdapter.THEME_NOTE_IN_ID +")" +
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
        int index = getNoteIndex(id);

        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.THEME_NOTE +
                " WHERE " + SQLiteDatabaseAdapter.THEME_NOTE_IN_ID + " = " + id);

        database.execSQL("UPDATE " + SQLiteDatabaseAdapter.THEME_NOTE +
                " SET " + SQLiteDatabaseAdapter.THEME_NOTE_INDEX + " = " + SQLiteDatabaseAdapter.THEME_NOTE_INDEX + " - 1" +
                " WHERE " + SQLiteDatabaseAdapter.THEME_NOTE_INDEX + " > " + index);
    }

    public static void translateNoteUp(int id, int inId) {
        int index = getNoteIndex(inId);
        int secondInId;
        boolean isTheme = false;
        try {
            secondInId = ThemeIntoManager.getThemeId(id, index - 1);
            isTheme = true;
        } catch (NoSuchElementException e) {
            secondInId = getNoteId(id, index - 1);
        }

        setNoteIndex(inId, index - 1);
        if (isTheme) ThemeIntoManager.setThemeIndex(secondInId, index);
        else setNoteIndex(secondInId, index);
    }

    public static void translateNoteDown(int id, int inId) {
        int index = getNoteIndex(inId);
        int secondInId;
        boolean isTheme = false;
        try {
            secondInId = ThemeIntoManager.getThemeId(id, index + 1);
            isTheme = true;
        } catch (NoSuchElementException e) {
            secondInId = getNoteId(id, index + 1);
        }

        setNoteIndex(inId, index + 1);
        if (isTheme) ThemeIntoManager.setThemeIndex(secondInId, index);
        else setNoteIndex(secondInId, index);
    }

    public static void setNoteIndex(int id, int index) {
        database.execSQL("UPDATE " + SQLiteDatabaseAdapter.THEME_NOTE +
                " SET " + SQLiteDatabaseAdapter.THEME_NOTE_INDEX + " = " + index +
                " WHERE " + SQLiteDatabaseAdapter.THEME_NOTE_IN_ID + " = " + id);
    }
}
