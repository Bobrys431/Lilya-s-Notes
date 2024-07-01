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

import com.example.lilyasnotes.Data.DTO.Theme;
import com.example.lilyasnotes.EmergentWidgets.EmergentWidget;
import com.example.lilyasnotes.EmergentWidgets.MainEmergentWidget;
import com.example.lilyasnotes.Widgets.UndoEraseWidget;
import com.example.lilyasnotes.RecyclerViewAdapters.AbstractRecyclerViewAdapter;
import com.example.lilyasnotes.RecyclerViewAdapters.MainRecyclerViewAdapter;
import com.example.lilyasnotes.R;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.RecyclerViewAdapters.RecyclerViewMoveCallback;
import com.example.lilyasnotes.SearchBars.MainSearchBarHelper;
import com.example.lilyasnotes.SearchBars.AbstractSearchBarHelper;

public class MainActivity extends AbstractActivity {

    private MainRecyclerViewAdapter adapter;
    private MainSearchBarHelper searchBar;
    private EmergentWidget emergentWidget;
    private UndoEraseWidget undoEraseWidget;

    private RecyclerView dataListView;
    private ImageView dataListBackground;
    private RelativeLayout actionBarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteDatabaseAdapter.printTable(null, this);
        loadThemesFromDatabaseIntoThemesList();

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


    private void loadThemesFromDatabaseIntoThemesList() {
        Cursor databaseCursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEMES_THEME_ID +
                " FROM " + SQLiteDatabaseAdapter.THEMES +
                " ORDER BY " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " ASC", null);

        if (databaseCursor != null) {
            while (databaseCursor.moveToNext()) {
                data.add(new Theme(
                        databaseCursor.getInt(databaseCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEMES_THEME_ID)),
                        this));
            }
            databaseCursor.close();
        }
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
        dataListView = findViewById(R.id.themes_list_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        dataListView.setLayoutManager(layoutManager);

        adapter = new MainRecyclerViewAdapter(this);
        adapter.setThemes(data);

        ItemTouchHelper.Callback callback = new RecyclerViewMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(dataListView);

        dataListView.setAdapter(adapter);
    }


    @Override
    protected void buildScrollingBackground() {
        dataListBackground = findViewById(R.id.themes_list_background);
        dataListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                float translationY = dataListBackground.getTranslationY() - dy * 0.5f;
                dataListBackground.setTranslationY(translationY);

                float scaleFactor = 1 - 0.2f * translationY / dataListBackground.getHeight();
                dataListBackground.setScaleX(scaleFactor);
                dataListBackground.setScaleY(scaleFactor);
            }
        });
    }

    @Override
    protected void buildSearchBar() {
        searchBar = new MainSearchBarHelper(this);
        searchBar.build();
    }

    @Override
    protected void buildEmergentWidget() {
        emergentWidget = new MainEmergentWidget(this);
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