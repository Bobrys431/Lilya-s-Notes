package com.example.lilyasnotes.Utilities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;

public class Tools {
    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static void clearDB(Context context) {
        SQLiteDatabase database = SQLiteDatabaseAdapter.getDatabase(context);

        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.ADDITIONAL_DATA);
        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.THEME);
        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.NOTE);
        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.THEMES);
        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.THEME_NOTE);
        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.THEME_INTO);
    }

    public static int getDataLength(Context context, int id) {
        SQLiteDatabase database = SQLiteDatabaseAdapter.getDatabase(context);

        Cursor themeCount = database.rawQuery("SELECT COUNT(" + SQLiteDatabaseAdapter.THEME_INTO_ID + ") AS count" +
                " FROM " + SQLiteDatabaseAdapter.THEME_INTO +
                " WHERE " + SQLiteDatabaseAdapter.THEME_INTO_THEME_ID + " = " + id, null);

        Cursor noteCount = database.rawQuery("SELECT COUNT(" + SQLiteDatabaseAdapter.THEME_NOTE_ID + ") AS count" +
                " FROM " + SQLiteDatabaseAdapter.THEME_NOTE +
                " WHERE " + SQLiteDatabaseAdapter.THEME_NOTE_THEME_ID + " = " + id, null);

        int count = 0;
        if (themeCount != null && noteCount != null) {
            if (themeCount.moveToFirst()) {
                count += themeCount.getInt(themeCount.getColumnIndexOrThrow("count"));
            }
            if (noteCount.moveToFirst()) {
                count += noteCount.getInt(noteCount.getColumnIndexOrThrow("count"));
            }
            themeCount.close();
            noteCount.close();
        }
        return count;
    }
}
