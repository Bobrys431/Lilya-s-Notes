package com.example.lilyasnotes.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.NoSuchElementException;

public class ThemeManager {

    private static SQLiteDatabase database;

    public static void build(Context context) {
        database = SQLiteDatabaseAdapter.getDatabase(context);
    }

    public static String getTitle(int id) {
        Cursor titleById = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_TITLE +
                " FROM " + SQLiteDatabaseAdapter.THEME +
                " WHERE " + SQLiteDatabaseAdapter.THEME_ID + " = " + id, null);

        if (titleById != null && titleById.moveToFirst()) {
            String title = titleById.getString(titleById.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_TITLE));
            titleById.close();
            return title;
        }
        throw new NoSuchElementException("Missing Theme with id " + id);
    }

    public static void setTitle(int id, String title) {
        database.execSQL("UPDATE " + SQLiteDatabaseAdapter.THEME +
                " SET " + SQLiteDatabaseAdapter.THEME_TITLE + " = ?" +
                " WHERE " + SQLiteDatabaseAdapter.THEME_ID + " = " + id, new Object[]{title});
    }

    public static void addNewTheme(String title) {
        database.execSQL("INSERT INTO " + SQLiteDatabaseAdapter.THEME + "(" + SQLiteDatabaseAdapter.THEME_TITLE + ")" +
                " VALUES (?)", new Object[]{title});
    }

    public static int getLastThemeId() {
        Cursor lastThemeId = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_ID +
                " FROM " + SQLiteDatabaseAdapter.THEME +
                " ORDER BY " + SQLiteDatabaseAdapter.THEME_ID + " DESC " +
                " LIMIT 1", null);

        int id = -1;

        if (lastThemeId != null && lastThemeId.moveToFirst()) {
            id = lastThemeId.getInt(lastThemeId.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_ID));
            lastThemeId.close();
        }

        return id;
    }

    public static void deleteTheme(int id) {
        deleteThemesInTheme(id);
        deleteNotesInTheme(id);

        deleteThemeFromDatabase(id);
        ThemesManager.deleteThemeFromThemes(id);
        ThemeIntoManager.deleteConnection(id);
    }

    private static void deleteThemesInTheme(int id) {
        Cursor cursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_INTO_IN_ID +
                " FROM " + SQLiteDatabaseAdapter.THEME_INTO +
                " WHERE " + SQLiteDatabaseAdapter.THEME_INTO_THEME_ID + " = " + id, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                deleteTheme(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_INTO_IN_ID)));
            }
            cursor.close();
        }
    }

    private static void deleteNotesInTheme(int id) {
        Cursor cursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_NOTE_IN_ID +
                " FROM " + SQLiteDatabaseAdapter.THEME_NOTE +
                " WHERE " + SQLiteDatabaseAdapter.THEME_NOTE_THEME_ID + " = " + id, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                NoteManager.deleteNote(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_NOTE_IN_ID)));
            }
            cursor.close();
        }
    }

    private static void deleteThemeFromDatabase(int id) {
        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.THEME +
                " WHERE " + SQLiteDatabaseAdapter.THEME_ID + " = " + id);
    }
}
