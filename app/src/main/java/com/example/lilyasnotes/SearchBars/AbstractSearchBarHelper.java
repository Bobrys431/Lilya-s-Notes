package com.example.lilyasnotes.SearchBars;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.lilyasnotes.Activities.AbstractActivity;
import com.example.lilyasnotes.Data.DTO.Data;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSearchBarHelper {

    public boolean isSearching;
    protected List<Data> visibleData;
    protected AbstractActivity activity;

    protected EditText searchBar;
    private final RelativeLayout searchField;
    private final ImageView searchIcon;

    public AbstractSearchBarHelper(AbstractActivity activity) {
        this.activity = activity;

        this.searchBar = activity.findViewById(R.id.search_bar);
        this.searchField = activity.findViewById(R.id.search_field);
        this.searchIcon = activity.findViewById(R.id.search);

        visibleData = new ArrayList<>();
    }

    public void build() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { /* **NOTHING** */ }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateVisibleDataAccordingSequence(charSequence);
                recordToRecordingListAndNotifyAdapter();
            }

            @Override
            public void afterTextChanged(Editable editable) { /* **NOTHING** */ }
        });
    }

    public void updateVisibleData() {
        updateVisibleDataAccordingSequence(searchBar.getText());
        recordToRecordingList();
    }

    private void updateVisibleDataAccordingSequence(CharSequence charSequence) {
        System.out.println("SearchBarHelper updateVisibleDataAccordingSequence");

        visibleData.clear();

        if (charSequence.length() == 0) {
            insertAllDataFromDatabaseToVisibleData();
            isSearching = false;
        } else {
            compareDataFromDatabaseAndInsertToVisibleData(charSequence);
            isSearching = true;
        }
    }

    protected abstract void insertAllDataFromDatabaseToVisibleData();

    protected abstract void compareDataFromDatabaseAndInsertToVisibleData(CharSequence charSequence);

    protected abstract void recordToRecordingList();

    protected abstract void recordToRecordingListAndNotifyAdapter();

    @SuppressLint("DiscouragedApi")
    public void changeColorByAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);

        searchBar.setTextColor(activity.getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeActiveColor :
                        R.color.darkThemeBackground
        ));
        searchField.setBackgroundResource(activity.getResources().getIdentifier(
                "search_field_" + appTheme,
                "drawable",
                activity.getPackageName()
        ));
        searchIcon.setImageResource(activity.getResources().getIdentifier(
                "search_" + appTheme,
                "drawable",
                activity.getPackageName()
        ));
    }
}