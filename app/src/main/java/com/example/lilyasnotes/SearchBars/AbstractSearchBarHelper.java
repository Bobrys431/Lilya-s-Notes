package com.example.lilyasnotes.SearchBars;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;

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
                if (activity.eraseMode) {
                    activity.eraseMode = false;
                    new Handler().postDelayed(() -> {
                        activity.eraseMode = true;
                        activity.getFooterView().notifyEraseModeChanged();
                    }, 180);
                }
                updateVisibleDataAccordingSequence(charSequence);
                recordToRecordingListAndNotifyAdapter();
            }

            @Override
            public void afterTextChanged(Editable editable) { /* **NOTHING** */ }
        });

        buildOnBackPressed();
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

    private void buildOnBackPressed() {
        activity.getOnBackPressedDispatcher().addCallback(activity, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (searchBar.isFocused()) {
                    searchBar.setEnabled(false);
                    searchBar.setEnabled(true);
                } else {
                    setEnabled(false);
                    activity.getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        });
    }

    public void changeColorByAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);

        changeSearchBarColor();
        changeSearchFieldColor(appTheme);
        changeSearchIconColor(appTheme);
    }

    private void changeSearchBarColor() {
        searchBar.setTextColor(activity.getColor(R.color.lightThemeActiveColor));
        searchBar.setHintTextColor(activity.getColor(R.color.lightThemeHintColor));
    }

    @SuppressLint("DiscouragedApi")
    private void changeSearchFieldColor(String appTheme) {
        searchField.setBackgroundResource(activity.getResources().getIdentifier(
                "search_field_" + appTheme,
                "drawable",
                activity.getPackageName()
        ));
    }

    @SuppressLint("DiscouragedApi")
    private void changeSearchIconColor(String appTheme) {
        searchIcon.setImageResource(activity.getResources().getIdentifier(
                "search_" + appTheme,
                "drawable",
                activity.getPackageName()
        ));
    }
}