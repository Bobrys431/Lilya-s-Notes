package com.example.lilyasnotes.Activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.ButtonManagers.ButtonsManager;
import com.example.lilyasnotes.Data.DTO.Data;
import com.example.lilyasnotes.Data.DTO.Theme;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;
import com.example.lilyasnotes.RecyclerViews.RecyclerViewAdapters.ThemeRecyclerViewAdapter;
import com.example.lilyasnotes.Widgets.SearchBars.SearchBarHelper;

import java.util.ArrayList;
import java.util.List;

public class ThemeActivity extends AppCompatActivity {

    final public static int NO_TYPE = -1;
    final public static int THEME_TYPE = 0;
    final public static int NOTE_TYPE = 1;

    public Theme theme;

    public int selectedViewId;
    public int selectedViewType;
    public List<Data> data;
    public RecyclerView dataListView;
    public ButtonsManager buttonsManager;
    public ThemeRecyclerViewAdapter adapter;
    private SQLiteDatabase database;
    private SearchBarHelper searchBar;

    private ImageView themesListBackground;
    private ImageButton themeButton;
    private ImageButton exitButton;
    private RelativeLayout actionBarLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        database = SQLiteDatabaseAdapter.getDatabase(this);
        data = new ArrayList<>();
        selectedViewId = NO_TYPE;
        selectedViewType = NO_TYPE;

        SQLiteDatabaseAdapter.printTable(null, this);
        loadDataFromDatabaseIntoDataList();

        buildRecyclerView();
        buildScrollingBackground();
        buildButtons();
        buildSearchBar();
        getAndSetAppTheme();
    }


    private void loadDataFromDatabaseIntoDataList() {

    }

    private void buildRecyclerView() {
    }

    private void buildScrollingBackground() {
    }

    private void buildButtons() {
    }

    private void buildSearchBar() {
    }

    private void getAndSetAppTheme() {
    }

    public void reloadData() {
    }

    public SearchBarHelper getSearchBar() {
        return searchBar;
    }
}
