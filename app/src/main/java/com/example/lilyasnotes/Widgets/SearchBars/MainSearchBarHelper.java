package com.example.lilyasnotes.Widgets.SearchBars;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;

import com.example.lilyasnotes.Activities.MainActivity;
import com.example.lilyasnotes.Data.DTO.Theme;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.Database.ThemeManager;

import java.util.Collection;
import java.util.Collections;

public class MainSearchBarHelper extends SearchBarHelper {

    MainActivity activity;

    public MainSearchBarHelper(EditText searchBar, MainActivity activity) {
        super(searchBar);
        this.activity = activity;
    }

    @Override
    protected void insertAllDataFromDatabaseToVisibleData() {
        Theme[] themes = getAllThemes();
        Collections.addAll(visibleData, themes);
    }

    @Override
    protected void compareDataFromDatabaseAndInsertToVisibleData(CharSequence charSequence) {
        Theme[] themes = getAllThemes();

        for (Theme theme : themes) {
            if (isThemeTitleFoundBySequence(
                    theme.id,
                    charSequence
            )) {
                visibleData.add(theme);
            }
        }
    }

    private boolean isThemeTitleFoundBySequence(int id, CharSequence charSequence) {
        String title = ThemeManager.getTitle(id);

        if (title == null) return false;
        return title.contains(charSequence);
    }

    private Theme[] getAllThemes() {
        SQLiteDatabase database = SQLiteDatabaseAdapter.getDatabase(activity);

        Cursor allThemesCursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEMES_THEME_ID +
                ", " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX +
                " FROM " + SQLiteDatabaseAdapter.THEMES +
                " ORDER BY " + SQLiteDatabaseAdapter.THEME_INTO_INDEX + " ASC", null);

        Theme[] themes = new Theme[getThemesLength()];

        if (allThemesCursor != null) {
            while (allThemesCursor.moveToNext()) {
                themes[allThemesCursor.getInt(allThemesCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEMES_THEME_INDEX))]
                        = new Theme(allThemesCursor.getInt(allThemesCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEMES_THEME_ID)), activity);
            }
            allThemesCursor.close();
        }

        return themes;
    }

    private int getThemesLength() {
        SQLiteDatabase database = SQLiteDatabaseAdapter.getDatabase(activity);

        Cursor themeCount = database.rawQuery("SELECT COUNT(" + SQLiteDatabaseAdapter.THEMES_ID + ") AS count" +
                " FROM " + SQLiteDatabaseAdapter.THEMES, null);

        int count = 0;
        if (themeCount != null) {
            if (themeCount.moveToFirst()) {
                count += themeCount.getInt(themeCount.getColumnIndexOrThrow("count"));
            }
            themeCount.close();
        }
        return count;
    }

    @Override
    protected void recordToRecordingList() {
        activity.themes.clear();
        activity.themes.addAll((Collection<? extends Theme>) visibleData);
    }
}
