package com.example.lilyasnotes.Data.DTO;

import android.content.Context;
import android.database.Cursor;

import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;

public class Note implements Data {

    final public int id;
    public String title;
    public String text;

    public Note(int id, Context context) {
        this.id = id;

        Cursor noteCursor = SQLiteDatabaseAdapter.getDatabase(context).rawQuery("SELECT " + SQLiteDatabaseAdapter.NOTE_TITLE +
                ", " + SQLiteDatabaseAdapter.NOTE_TEXT +
                " FROM " + SQLiteDatabaseAdapter.NOTE +
                " WHERE " + SQLiteDatabaseAdapter.NOTE_ID + " = " + id, null);
        if (noteCursor != null && noteCursor.moveToFirst()) {
            title = noteCursor.getString(noteCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.NOTE_TITLE));
            text = noteCursor.getString(noteCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.NOTE_TEXT));
            noteCursor.close();
        }
    }
}
