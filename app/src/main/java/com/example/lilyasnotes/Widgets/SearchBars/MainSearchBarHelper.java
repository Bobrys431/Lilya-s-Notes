package com.example.lilyasnotes.Widgets.SearchBars;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;

import com.example.lilyasnotes.Activities.MainActivity;
import com.example.lilyasnotes.Data.DTO.Data;
import com.example.lilyasnotes.Data.DTO.Theme;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.Database.ThemeManager;

public class MainSearchBarHelper extends SearchBarHelper {

    MainActivity activity;

    public MainSearchBarHelper(EditText searchBar, MainActivity activity) {
        super(searchBar);
        this.activity = activity;
    }

    @Override
    protected void insertAllThemesFromDatabaseToVisibleData() {
        SQLiteDatabase database = SQLiteDatabaseAdapter.getDatabase(context);

        Cursor allThemesId = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEMES_THEME_ID +
                " FROM " + SQLiteDatabaseAdapter.THEMES +
                " ORDER BY " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " ASC", null);

        if (allThemesId != null) {
            while (allThemesId.moveToNext()) {
                visibleData.add(new Theme(
                        allThemesId.getInt(allThemesId.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEMES_THEME_ID)),
                        context));
            }
            allThemesId.close();
        }
    }

    @Override
    protected void compareThemesFromDatabaseAndInsertToVisibleData(CharSequence charSequence) {
        SQLiteDatabase database = SQLiteDatabaseAdapter.getDatabase(context);

        Cursor themeByMain = database.rawQuery("SELECT " +
                SQLiteDatabaseAdapter.THEME_ID +
                " FROM " + SQLiteDatabaseAdapter.THEME +
                " INNER JOIN " + SQLiteDatabaseAdapter.THEMES + " USING(" + SQLiteDatabaseAdapter.THEME_ID + ")" +
                " ORDER BY " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " ASC", null);

        while (themeByMain.moveToNext()) {
            int themeToCheckId = themeByMain.getInt(themeByMain.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_ID));

            if (isThemeTitleFoundBySequence(
                    charSequence,
                    themeToCheckId
            )) {
                addThemeToVisibleData(themeToCheckId);
                continue;
            }

            if (isInThemeTitleFoundBySequence(
                    charSequence,
                    themeToCheckId
            )) {
                addThemeToVisibleData(themeToCheckId);
                continue;
            }

            if (isInThemeTextFoundBySequence(
                    charSequence,
                    themeToCheckId
            )) {
                addThemeToVisibleData(themeToCheckId);
            }
        }
        themeByMain.close();

        for (Data data : visibleData) {
            Theme theme = (Theme) data;
            System.out.println(theme.title + " has recorded!");
        }
    }

    private boolean isThemeTitleFoundBySequence(CharSequence charSequence, int id) {
        String title = ThemeManager.getTitle(id);

        if (title == null) return false;
        System.out.println(title + " = " + title.contains(charSequence));
        return title.contains(charSequence);
    }

    private boolean isInThemeTitleFoundBySequence(CharSequence charSequence, int id) {
        SQLiteDatabase database = SQLiteDatabaseAdapter.getDatabase(context);

        Cursor titlesInTheme = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_TITLE +
                " FROM " + SQLiteDatabaseAdapter.THEME +
                " INNER JOIN " + SQLiteDatabaseAdapter.THEME_INTO +
                " ON " + SQLiteDatabaseAdapter.THEME_INTO + "." + SQLiteDatabaseAdapter.THEME_INTO_IN_ID +
                " = " + SQLiteDatabaseAdapter.THEME + "." + SQLiteDatabaseAdapter.THEME_ID +
                " WHERE " + SQLiteDatabaseAdapter.THEME_INTO + "." + SQLiteDatabaseAdapter.THEME_INTO_THEME_ID + " = " + id +
                " ORDER BY " + SQLiteDatabaseAdapter.THEME_INTO_INDEX + " ASC", null);

        boolean isTitleFoundBySequence = false;

        while (titlesInTheme.moveToNext()) {
            String titleIn = titlesInTheme.getString(titlesInTheme.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_TITLE));

            if (titleIn.contains(charSequence)) {
                isTitleFoundBySequence = true;
                break;
            }
        }

        titlesInTheme.close();
        return isTitleFoundBySequence;
    }

    private boolean isInThemeTextFoundBySequence(CharSequence charSequence, int id) {

        SQLiteDatabase database = SQLiteDatabaseAdapter.getDatabase(context);

        Cursor notesInTheme = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_NOTE_IN_ID +
                " FROM " + SQLiteDatabaseAdapter.THEME_NOTE +
                " WHERE " + SQLiteDatabaseAdapter.THEME_NOTE_THEME_ID + " = " + id +
                " ORDER BY " + SQLiteDatabaseAdapter.THEME_NOTE_INDEX + " ASC", null);

        boolean isTitleFoundBySequence = false;

        while (notesInTheme.moveToNext()) {
            String textIn = notesInTheme.getString(notesInTheme.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_NOTE_IN_ID));

            if (textIn.contains(charSequence)) {
                isTitleFoundBySequence = true;
                break;
            }
        }

        notesInTheme.close();
        return isTitleFoundBySequence;
    }

    private void addThemeToVisibleData(int id) {
        Theme theme = new Theme(id, context);
        visibleData.add(theme);
    }

    @Override
    protected void recordToRecordingList() {
        activity.themes.clear();

        for (Data data : visibleData) {
            Theme theme = (Theme) data;
            activity.themes.add(theme);
        }
    }
}
