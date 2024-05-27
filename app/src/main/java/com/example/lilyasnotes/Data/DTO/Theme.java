package com.example.lilyasnotes.Data.DTO;

import android.content.Context;
import android.database.Cursor;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;

public class Theme implements Data {
    final public int id;
    public String title;


    public Theme(int id, Context context) {
        this.id = id;

        Cursor themeCursor = SQLiteDatabaseAdapter.getDatabase(context).rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_TITLE +
                " FROM " + SQLiteDatabaseAdapter.THEME +
                " WHERE " + SQLiteDatabaseAdapter.THEME_ID + " = " + id, null);
        if (themeCursor != null && themeCursor.moveToFirst()) {
            title = themeCursor.getString(themeCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_TITLE));
            themeCursor.close();
        }
    }
}
