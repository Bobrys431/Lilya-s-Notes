package com.example.lilyasnotes.Utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;

public class Tools {
    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
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

    @SuppressLint({"DiscouragedApi", "InternalInsetResource"})
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
