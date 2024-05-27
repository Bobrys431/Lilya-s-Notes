package com.example.lilyasnotes.DatabaseManagement;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.NoSuchElementException;

public class NoteManager {

    private static SQLiteDatabase database;

    public static void build(Context context) {
        database = SQLiteDatabaseAdapter.getDatabase(context);
    }

    public static String getTitle(int id) {
        System.out.println("NoteManager getTitle");

        Cursor titleById = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.NOTE_TITLE +
                " FROM " + SQLiteDatabaseAdapter.NOTE +
                " WHERE " + SQLiteDatabaseAdapter.NOTE_ID + " = " + id, null);

        if (titleById != null && titleById.moveToFirst()) {
            String title = titleById.getString(titleById.getColumnIndexOrThrow(SQLiteDatabaseAdapter.NOTE_TITLE));
            titleById.close();
            return title;
        }
        throw new NoSuchElementException("Missing Note with id " + id);
    }

    public static String getText(int id) {
        System.out.println("NoteManager getText");

        Cursor titleById = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.NOTE_TEXT +
                " FROM " + SQLiteDatabaseAdapter.NOTE +
                " WHERE " + SQLiteDatabaseAdapter.NOTE_ID + " = " + id, null);

        if (titleById != null && titleById.moveToFirst()) {
            String title = titleById.getString(titleById.getColumnIndexOrThrow(SQLiteDatabaseAdapter.NOTE_TEXT));
            titleById.close();
            return title;
        }
        throw new NoSuchElementException("Missing Note with id " + id);
    }

    public static void setTitle(int id, String title) {
        System.out.println("NoteManager setTitle");

        database.execSQL("UPDATE " + SQLiteDatabaseAdapter.NOTE +
                " SET " + SQLiteDatabaseAdapter.NOTE_TITLE + " = ?" +
                " WHERE " + SQLiteDatabaseAdapter.NOTE_ID + " = " + id, new Object[]{title});
    }

    public static void setText(int id, String text) {
        System.out.println("NoteManager setText");

        database.execSQL("UPDATE " + SQLiteDatabaseAdapter.NOTE +
                " SET " + SQLiteDatabaseAdapter.NOTE_TEXT + " = ?" +
                " WHERE " + SQLiteDatabaseAdapter.NOTE_ID + " = " + id, new Object[]{text});
    }

    public static void addNewNote(String title, String text) {
        database.execSQL("INSERT INTO " + SQLiteDatabaseAdapter.NOTE + "(" +
                SQLiteDatabaseAdapter.NOTE_TITLE + ", " +
                SQLiteDatabaseAdapter.NOTE_TEXT + ")" +
                " VALUES (?, ?)", new Object[]{title, text});
    }

    public static int getLastNoteId() {
        Cursor lastNoteId = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.NOTE_ID +
                " FROM " + SQLiteDatabaseAdapter.NOTE +
                " ORDER BY " + SQLiteDatabaseAdapter.NOTE_ID + " DESC " +
                " LIMIT 1", null);

        int id = -1;

        if (lastNoteId != null && lastNoteId.moveToFirst()) {
            id = lastNoteId.getInt(lastNoteId.getColumnIndexOrThrow(SQLiteDatabaseAdapter.NOTE_ID));
            lastNoteId.close();
        }

        return id;
    }

    public static void deleteNote(int id) {
        deleteNoteFromDatabase(id);
        ThemeNoteManager.deleteConnection(id);
    }

    private static void deleteNoteFromDatabase(int id) {
        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.NOTE +
                " WHERE " + SQLiteDatabaseAdapter.NOTE_ID + " = " + id);
    }
}
