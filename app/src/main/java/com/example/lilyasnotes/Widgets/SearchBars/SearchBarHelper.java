package com.example.lilyasnotes.Widgets.SearchBars;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.lilyasnotes.ButtonManagers.ButtonsManagerUpdater;
import com.example.lilyasnotes.Data.DTO.Data;
import com.example.lilyasnotes.RecyclerViews.Additions.RecyclerViewUpdater;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchBarHelper {

    public boolean isSearching;
    protected List<Data> visibleData;

    protected EditText searchBar;
    protected Context context;
    private RecyclerViewUpdater recyclerViewUpdater;
    private ButtonsManagerUpdater buttonsManagerUpdater;


    public SearchBarHelper(EditText searchBar) {
        this.searchBar = searchBar;
        this.context = searchBar.getContext();
        visibleData = new ArrayList<>();
    }

    public void setRecyclerViewUpdater(RecyclerViewUpdater recyclerViewUpdater) {
        this.recyclerViewUpdater = recyclerViewUpdater;
    }

    public void setButtonsManagerUpdater(ButtonsManagerUpdater buttonsManagerUpdater) {
        this.buttonsManagerUpdater = buttonsManagerUpdater;
    }

    public void build() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { /* **NOTHING** */ }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (recyclerViewUpdater != null) {
                    recyclerViewUpdater.updateAdapterDataSet();
                }
                updateVisibleDataAccordingSequence(charSequence);
                recordToRecordingList();
                if (recyclerViewUpdater != null) {
                    recyclerViewUpdater.updateAdapterDataSet();
                }
                if (buttonsManagerUpdater != null) {
                    buttonsManagerUpdater.updateButtons();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { /* **NOTHING** */ }
        });
    }

    private void updateVisibleDataAccordingSequence(CharSequence charSequence) {
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
}
