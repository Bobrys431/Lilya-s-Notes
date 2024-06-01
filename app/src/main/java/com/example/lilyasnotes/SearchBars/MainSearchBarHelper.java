package com.example.lilyasnotes.SearchBars;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lilyasnotes.Activities.MainActivity;
import com.example.lilyasnotes.Data.DTO.Theme;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.DatabaseManagement.ThemeManager;

public class MainSearchBarHelper extends AbstractSearchBarHelper {

    public MainSearchBarHelper(MainActivity activity) {
        super(activity);
    }

    @Override
    protected void insertAllDataFromDatabaseToVisibleData() {
        Theme[] themes = getAllThemes();

        for (Theme theme : themes) {
            if (isThemeNotGoingToBeDeleted(theme)) {
                visibleData.add(theme);
            }
        }
    }

    @Override
    protected void compareDataFromDatabaseAndInsertToVisibleData(CharSequence charSequence) {
        Theme[] themes = getAllThemes();

        for (Theme theme : themes) {
            if (
                    isThemeNotGoingToBeDeleted(theme) &&
                    isThemeTitleFoundBySequence(theme.id, charSequence)
            ) {
                visibleData.add(theme);
            }
        }
    }

    private boolean isThemeNotGoingToBeDeleted(Theme theme) {
        return
                !activity.getUndoEraseWidget().isActive() ||
                !(activity.getUndoEraseWidget().getItem() instanceof Theme) ||
                ((Theme) activity.getUndoEraseWidget().getItem()).id != theme.id;
    }

    private boolean isThemeTitleFoundBySequence(int id, CharSequence charSequence) {
        String title = ThemeManager.getTitle(id);

        if (title == null) return false;
        return title.toLowerCase().contains(charSequence.toString().toLowerCase());
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
        activity.data.clear();
        activity.data.addAll(visibleData);
    }

    @Override
    protected void recordToRecordingListAndNotifyAdapter() {
        int size = activity.data.size();
        activity.data.clear();
        activity.getAdapter().notifyItemRangeRemoved(0, size);

        activity.data.addAll(visibleData);
        activity.getAdapter().notifyItemRangeInserted(0, activity.data.size());
    }
}
