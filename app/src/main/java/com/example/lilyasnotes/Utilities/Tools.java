package com.example.lilyasnotes.Utilities;

import android.content.Context;
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
}
