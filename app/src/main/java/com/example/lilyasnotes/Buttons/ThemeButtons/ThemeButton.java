package com.example.lilyasnotes.Buttons.ThemeButtons;

import android.content.Context;

import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;

public abstract class ThemeButton {

    private final Context context;

    public ThemeButton(Context context) {
        this.context = context;
    }

    public void changeAppTheme() {
        SQLiteDatabaseAdapter.getDatabase(context).execSQL("UPDATE " + SQLiteDatabaseAdapter.ADDITIONAL_DATA + " SET " + SQLiteDatabaseAdapter.APP_THEME + " = CASE WHEN " + SQLiteDatabaseAdapter.APP_THEME + " = 'LIGHT' THEN 'DARK' ELSE 'LIGHT' END");
        changeAllViewsByAppTheme();
    }

    public abstract void changeAllViewsByAppTheme();
}
