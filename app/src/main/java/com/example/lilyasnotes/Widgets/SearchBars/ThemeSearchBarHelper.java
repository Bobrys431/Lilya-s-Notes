package com.example.lilyasnotes.Widgets.SearchBars;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lilyasnotes.Activities.ThemeActivity;
import com.example.lilyasnotes.Data.DTO.Data;
import com.example.lilyasnotes.Data.DTO.Note;
import com.example.lilyasnotes.Data.DTO.Theme;
import com.example.lilyasnotes.Database.NoteManager;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.Database.ThemeManager;
import com.example.lilyasnotes.Utilities.Tools;

import java.util.Collections;

public class ThemeSearchBarHelper extends AbstractSearchBarHelper {

    public ThemeSearchBarHelper(ThemeActivity activity) {
        super(activity);
    }

    @Override
    protected void insertAllDataFromDatabaseToVisibleData() {
        Data[] jointData = getAllData();
        Collections.addAll(visibleData, jointData);
    }

    @Override
    protected void compareDataFromDatabaseAndInsertToVisibleData(CharSequence charSequence) {
        Data[] jointData = getAllData();

        for (Data data : jointData) {
            if (data instanceof Theme) {
                if (isThemeTitleFoundBySequence(
                        ((Theme) data).id,
                        charSequence
                )) {
                    visibleData.add(data);
                    continue;
                }
            }
            if (data instanceof Note) {
                if (isNoteTitleFoundBySequence(
                        ((Note) data).id,
                        charSequence
                )) {
                    visibleData.add(data);
                }
            }
        }
    }

    private boolean isThemeTitleFoundBySequence(int id, CharSequence charSequence) {
        String title = ThemeManager.getTitle(id);

        if (title == null) return false;
        return title.toLowerCase().contains(charSequence.toString().toLowerCase());
    }

    private boolean isNoteTitleFoundBySequence(int id, CharSequence charSequence) {
        String title = NoteManager.getTitle(id);

        if (title == null) return false;
        return title.toLowerCase().contains(charSequence.toString().toLowerCase());
    }

    private Data[] getAllData() {
        SQLiteDatabase database = SQLiteDatabaseAdapter.getDatabase(activity);

        Cursor allThemesCursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_INTO_IN_ID +
                ", " + SQLiteDatabaseAdapter.THEME_INTO_INDEX +
                " FROM " + SQLiteDatabaseAdapter.THEME_INTO +
                " WHERE " + SQLiteDatabaseAdapter.THEME_INTO_THEME_ID + " = " + ((ThemeActivity) activity).theme.id +
                " ORDER BY " + SQLiteDatabaseAdapter.THEME_INTO_INDEX + " ASC", null);

        Cursor allNotesCursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_NOTE_IN_ID +
                ", " + SQLiteDatabaseAdapter.THEME_NOTE_INDEX +
                " FROM " + SQLiteDatabaseAdapter.THEME_NOTE +
                " WHERE " + SQLiteDatabaseAdapter.THEME_NOTE_THEME_ID + " = " + ((ThemeActivity) activity).theme.id +
                " ORDER BY " + SQLiteDatabaseAdapter.THEME_NOTE_INDEX + " ASC", null);

        Data[] jointData = new Data[Tools.getDataLength(activity, ((ThemeActivity) activity).theme.id)];

        if (allThemesCursor != null && allNotesCursor != null) {
            while (allThemesCursor.moveToNext()) {
                jointData[allThemesCursor.getInt(allThemesCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_INTO_INDEX))]
                        = new Theme(allThemesCursor.getInt(allThemesCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_INTO_IN_ID)), activity);
            }
            while (allNotesCursor.moveToNext()) {
                jointData[allNotesCursor.getInt(allNotesCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_NOTE_INDEX))]
                        = new Note(allNotesCursor.getInt(allNotesCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_NOTE_IN_ID)), activity);
            }
            allThemesCursor.close();
            allNotesCursor.close();
        }

        return jointData;
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
