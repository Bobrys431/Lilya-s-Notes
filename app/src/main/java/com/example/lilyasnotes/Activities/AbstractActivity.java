package com.example.lilyasnotes.Activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Buttons.ButtonManagers.AbstractButtonsManager;
import com.example.lilyasnotes.Data.DTO.Data;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.RecyclerViews.AbstractRecyclerViewAdapter;
import com.example.lilyasnotes.Widgets.SearchBars.AbstractSearchBarHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractActivity extends AppCompatActivity {

    final public static byte NO_TYPE = -1;
    final public static byte THEME_TYPE = 0;
    final public static byte NOTE_TYPE = 1;

    public boolean eraseMode;

    public int selectedViewId;
    public byte selectedViewType;
    public List<Data> data;


    protected SQLiteDatabase database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedViewId = NO_TYPE;
        selectedViewType = NO_TYPE;
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
    public abstract RecyclerView getRecyclerView();
    public abstract ImageView getBackground();
    public abstract int getSelectedViewIndex();
}
