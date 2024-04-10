package com.example.lilyasnotes.Widgets.SearchBars;

import android.widget.EditText;

import com.example.lilyasnotes.Activities.ThemeActivity;

public class ThemeSearchBarHelper extends SearchBarHelper {

    ThemeActivity activity;

    public ThemeSearchBarHelper(EditText searchBar, ThemeActivity activity) {
        super(searchBar);
        this.activity = activity;
    }

    @Override
    protected void insertAllThemesFromDatabaseToVisibleData() {

    }

    @Override
    protected void compareThemesFromDatabaseAndInsertToVisibleData(CharSequence charSequence) {

    }

    @Override
    protected void recordToRecordingList() {

    }
}
