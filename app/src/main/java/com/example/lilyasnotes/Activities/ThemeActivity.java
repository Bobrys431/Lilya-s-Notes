package com.example.lilyasnotes.Activities;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Data.DTO.Data;
import com.example.lilyasnotes.Data.DTO.Note;
import com.example.lilyasnotes.Data.DTO.Theme;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.EmergentWidgets.EmergentWidget;
import com.example.lilyasnotes.EmergentWidgets.ThemeEmergentWidget;
import com.example.lilyasnotes.Widgets.UndoEraseWidget;
import com.example.lilyasnotes.R;
import com.example.lilyasnotes.RecyclerViewAdapters.AbstractRecyclerViewAdapter;
import com.example.lilyasnotes.RecyclerViewAdapters.RecyclerViewMoveCallback;
import com.example.lilyasnotes.RecyclerViewAdapters.ThemeRecyclerViewAdapter;
import com.example.lilyasnotes.Utilities.Tools;
import com.example.lilyasnotes.SearchBars.AbstractSearchBarHelper;
import com.example.lilyasnotes.SearchBars.ThemeSearchBarHelper;

import java.util.Arrays;

public class ThemeActivity extends AbstractActivity {

    public Theme theme;

    private ThemeRecyclerViewAdapter adapter;
    private ThemeSearchBarHelper searchBar;
    private UndoEraseWidget undoEraseWidget;
    private EmergentWidget emergentWidget;

    private RecyclerView dataListView;
    private ImageView dataListBackground;
    private RelativeLayout actionBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        theme = new Theme(getIntent().getIntExtra("themeId", -1), this);

        SQLiteDatabaseAdapter.printTable(null, this);
        loadDataFromDatabaseIntoDataList();

        buildStatusBar();
        buildActionBar();
        buildRecyclerView();
        buildScrollingBackground();
        buildSearchBar();
        buildEmergentWidget();
        buildEraseUndoSystem();
        buildKeyguardBehavior();

        emergentWidget.getThemeButton().changeAllViewsByAppTheme();
    }


    private void loadDataFromDatabaseIntoDataList() {
        Cursor allThemesCursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_INTO_IN_ID +
                ", " + SQLiteDatabaseAdapter.THEME_INTO_INDEX +
                " FROM " + SQLiteDatabaseAdapter.THEME_INTO +
                " WHERE " + SQLiteDatabaseAdapter.THEME_INTO_THEME_ID + " = " + theme.id +
                " ORDER BY " + SQLiteDatabaseAdapter.THEME_INTO_INDEX + " ASC", null);

        Cursor allNotesCursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_NOTE_IN_ID +
                ", " + SQLiteDatabaseAdapter.THEME_NOTE_INDEX +
                " FROM " + SQLiteDatabaseAdapter.THEME_NOTE +
                " WHERE " + SQLiteDatabaseAdapter.THEME_NOTE_THEME_ID + " = " + theme.id +
                " ORDER BY " + SQLiteDatabaseAdapter.THEME_NOTE_INDEX + " ASC", null);

        Data[] jointData = new Data[Tools.getDataLength(this, theme.id)];

        if (allThemesCursor != null && allNotesCursor != null) {
            while (allThemesCursor.moveToNext()) {
                jointData[allThemesCursor.getInt(allThemesCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_INTO_INDEX))]
                        = new Theme(allThemesCursor.getInt(allThemesCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_INTO_IN_ID)), this);
            }
            while (allNotesCursor.moveToNext()) {
                jointData[allNotesCursor.getInt(allNotesCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_NOTE_INDEX))]
                        = new Note(allNotesCursor.getInt(allNotesCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_NOTE_IN_ID)), this);
            }
            allThemesCursor.close();
            allNotesCursor.close();
        }

        data.addAll(Arrays.asList(jointData));
    }

    @Override
    protected void buildStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }

    @Override
    protected void buildActionBar() {
        actionBarLayout = findViewById(R.id.action_bar_layout);
    }

    @Override
    protected void buildRecyclerView() {
        dataListView = findViewById(R.id.data_list_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        dataListView.setLayoutManager(layoutManager);

        adapter = new ThemeRecyclerViewAdapter(this);
        adapter.setData(data);

        ItemTouchHelper.Callback callback = new RecyclerViewMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(dataListView);

        dataListView.setAdapter(adapter);
    }

    @Override
    protected void buildScrollingBackground() {
        dataListBackground = findViewById(R.id.data_list_background);
        dataListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                float translationY = dataListBackground.getTranslationY() - dy * 0.5f;
                dataListBackground.setTranslationY(translationY);
            }
        });
    }

    @Override
    protected void buildSearchBar() {
        searchBar = new ThemeSearchBarHelper(this);
        searchBar.build();
    }

    @Override
    protected void buildEmergentWidget() {
        emergentWidget = new ThemeEmergentWidget(this);
        emergentWidget.setup();
    }

    @Override
    protected void buildEraseUndoSystem() {
        undoEraseWidget = new UndoEraseWidget(this);
    }

    private void buildKeyguardBehavior() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility
                (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void reloadDataComparedToSearchBar() {
        searchBar.updateVisibleData();
    }

    @Override
    public AbstractRecyclerViewAdapter getAdapter() {
        return adapter;
    }

    @Override
    public AbstractSearchBarHelper getSearchBar() {
        return searchBar;
    }

    @Override
    public EmergentWidget getEmergentWidget() {
        return emergentWidget;
    }

    @Override
    public UndoEraseWidget getUndoEraseWidget() {
        return undoEraseWidget;
    }

    @Override
    public RecyclerView getDataListView() {
        return dataListView;
    }

    @Override
    public RelativeLayout getActionBarLayout() {
        return actionBarLayout;
    }

    @Override
    public ImageView getBackground() {
        return dataListBackground;
    }

    @Override
    protected void onResume() {
        super.onResume();
        emergentWidget.getThemeButton().changeAllViewsByAppTheme();
        reloadDataComparedToSearchBar();
        for (int i = 0; i < adapter.getItemCount(); i++) {
            adapter.notifyItemChanged(i);
        }
    }
}
