package com.example.lilyasnotes.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.NoSuchElementException;

public class ThemesManager {

    private static SQLiteDatabase database;

    public static void build(Context context) {
        database = SQLiteDatabaseAdapter.getDatabase(context);
    }

    public static int getThemeIndex(int id) {
        Cursor cursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX +
                " FROM " + SQLiteDatabaseAdapter.THEMES +
                " WHERE " + SQLiteDatabaseAdapter.THEMES_THEME_ID + " = " + id, null);

        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEMES_THEME_INDEX));
            cursor.close();
            return index;
        }
        throw new NoSuchElementException("Missing Theme with id " + id);
    }

    public static int getThemeId(int index) {
        Cursor cursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEMES_THEME_ID +
                " FROM " + SQLiteDatabaseAdapter.THEMES +
                " WHERE " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " = " + index, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEMES_THEME_ID));
            cursor.close();
            return id;
        }
        throw new NoSuchElementException("Missing Theme with index " + index);
    }

    public static void addNewTheme(int id) {
        database.execSQL("INSERT INTO " + SQLiteDatabaseAdapter.THEMES + "(" + SQLiteDatabaseAdapter.THEMES_THEME_ID + ", " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + ")" +
                "VALUES " + "(" + id + ", " + generateNewIndex() + ")");
    }

    private static int generateNewIndex() {
        Cursor lastThemeIndexCursor = database.rawQuery("SELECT COUNT(" + SQLiteDatabaseAdapter.THEMES_ID + ") AS max_index" +
                " FROM " + SQLiteDatabaseAdapter.THEMES, null);

        if (lastThemeIndexCursor != null && lastThemeIndexCursor.moveToFirst()) {
            int index = lastThemeIndexCursor.getInt(lastThemeIndexCursor.getColumnIndexOrThrow("max_index"));
            lastThemeIndexCursor.close();
            return index;
        }
        return 0;
    }

    public static void deleteThemeFromThemes(int id) {
        int index = getThemeIndex(id);
        if (index == -1) return;

        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.THEMES +
                " WHERE " + SQLiteDatabaseAdapter.THEMES_THEME_ID + " = " + id);

        database.execSQL("UPDATE " + SQLiteDatabaseAdapter.THEMES +
                " SET " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " = " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " - 1" +
                " WHERE " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " > " + index);
    }

    public static void translateThemeUp(int id) {
        int index = getThemeIndex(id);
        int secondId = getThemeId(index - 1);

        System.out.println("From: " + id + " = " + index);
        System.out.println("To: " + id + " = " + (index - 1));

        System.out.println("From: " + secondId + " = " + (index - 1));
        System.out.println("To: " + secondId + " = " + index);

        setThemeIndex(id, index - 1);
        setThemeIndex(secondId, index);
    }

    public static void translateThemeDown(int id) {
        int index = getThemeIndex(id);
        int secondId = getThemeId(index + 1);

        System.out.println("From: " + id + " = " + index);
        System.out.println("To: " + id + " = " + (index + 1));

        System.out.println("From: " + secondId + " = " + (index + 1));
        System.out.println("To: " + secondId + " = " + index);

        setThemeIndex(id, index + 1);
        setThemeIndex(secondId, index);
    }

    private static void setThemeIndex(int id, int index) {
        database.execSQL("UPDATE " + SQLiteDatabaseAdapter.THEMES +
                " SET " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " = " + index +
                " WHERE " + SQLiteDatabaseAdapter.THEME_ID + " = " + id);
    }
}
