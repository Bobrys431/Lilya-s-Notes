package com.example.lilyasnotes.Activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lilyasnotes.Buttons.ButtonManagers.AbstractButtonsManager;
import com.example.lilyasnotes.Data.DTO.Data;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.RecyclerViewAdapters.AbstractRecyclerViewAdapter;
import com.example.lilyasnotes.SearchBars.AbstractSearchBarHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractActivity extends AppCompatActivity {
    public boolean eraseMode;
    public List<Data> data;


    protected SQLiteDatabase database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        data = new ArrayList<>();
        eraseMode = false;

        database = SQLiteDatabaseAdapter.getDatabase(this);
    }

    protected abstract void buildRecyclerView();
    protected abstract void buildScrollingBackground();
    protected abstract void buildButtons();
    protected abstract void buildSearchBar();
    protected abstract void buildStatusAndActionBars();

    public abstract void reloadDataComparedToSearchBar();

    public abstract AbstractRecyclerViewAdapter getAdapter();
    public abstract AbstractButtonsManager getButtonsManager();
    public abstract AbstractSearchBarHelper getSearchBar();
    public abstract ImageView getBackground();
}
