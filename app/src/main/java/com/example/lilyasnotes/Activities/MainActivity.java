package com.example.lilyasnotes.Activities;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Buttons.ButtonManagers.AbstractButtonsManager;
import com.example.lilyasnotes.Buttons.ButtonManagers.MainButtonsManager;
import com.example.lilyasnotes.Data.DTO.Theme;
import com.example.lilyasnotes.Data.ViewHolders.ThemeViewHolder;
import com.example.lilyasnotes.Buttons.DTO.AddButton;
import com.example.lilyasnotes.Buttons.DTO.Button;
import com.example.lilyasnotes.Buttons.DTO.DeleteButton;
import com.example.lilyasnotes.Buttons.DTO.EditButton;
import com.example.lilyasnotes.RecyclerViews.AbstractRecyclerViewAdapter;
import com.example.lilyasnotes.RecyclerViews.MainRecyclerViewAdapter;
import com.example.lilyasnotes.R;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.RecyclerViews.RecyclerViewMoveCallback;
import com.example.lilyasnotes.Widgets.SearchBars.MainSearchBarHelper;
import com.example.lilyasnotes.Widgets.SearchBars.AbstractSearchBarHelper;

import java.util.NoSuchElementException;

public class MainActivity extends AbstractActivity {

    private MainButtonsManager buttonsManager;
    private MainRecyclerViewAdapter adapter;
    private MainSearchBarHelper searchBar;

    private RecyclerView themesListView;
    private ImageView themesListBackground;
    private ImageButton themeButton;
    private ImageButton exitButton;
    private RelativeLayout actionBarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteDatabaseAdapter.printTable(null, this);
        loadThemesFromDatabaseIntoThemesList();

        buildRecyclerView();
        buildScrollingBackground();
        buildButtons();
        buildSearchBar();
        buildStatusAndActionBars();

        changeByAppTheme();
        buttonsManager.updateButtonsDisplay();
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
    protected void buildRecyclerView() {
        themesListView = findViewById(R.id.themes_list_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        themesListView.setLayoutManager(layoutManager);

        adapter = new MainRecyclerViewAdapter(this);
        adapter.setRecyclerView(themesListView);
        adapter.setThemes(data);

        ItemTouchHelper.Callback callback = new RecyclerViewMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(themesListView);

        themesListView.setAdapter(adapter);
    }


    @Override
    protected void buildScrollingBackground() {
        themesListBackground = findViewById(R.id.themes_list_background);
        themesListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                float translationY = themesListBackground.getTranslationY() - dy * 0.5f;
                themesListBackground.setTranslationY(translationY);

                float scaleFactor = 1 - 0.2f * translationY / themesListBackground.getHeight();
                themesListBackground.setScaleX(scaleFactor);
                themesListBackground.setScaleY(scaleFactor);
            }
        });
    }

    @Override
    protected void buildButtons() {
        buildThemeButton();
        buildExitButton();

        buttonsManager = new MainButtonsManager(this);
        buttonsManager.addAndSetupButtonsByType(
                AddButton.class,
                DeleteButton.class,
                EditButton.class);
    }

    @SuppressLint("DiscouragedApi")
    private void buildThemeButton() {
        themeButton = findViewById(R.id.theme_button);
        themeButton.setOnClickListener(view -> changeAppTheme());
    }

    private void changeAppTheme() {
        database.execSQL("UPDATE " + SQLiteDatabaseAdapter.ADDITIONAL_DATA + " SET " + SQLiteDatabaseAdapter.APP_THEME + " = CASE WHEN " + SQLiteDatabaseAdapter.APP_THEME + " = 'LIGHT' THEN 'DARK' ELSE 'LIGHT' END");
        changeByAppTheme();
    }

    private void changeByAppTheme() {
        changeWindowColorByAppTheme();
        changeActionBarColorByAppTheme();
        changeStatusBarColorByAppTheme();
        changeAllButtonsColorByAppTheme();
        changeAllViewHoldersColorByAppTheme();
        changeSearchBarColorByAppTheme();
    }

    private void changeWindowColorByAppTheme() {
        Window window = getWindow();
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);

        window.setBackgroundDrawableResource(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground);
    }

    private void changeActionBarColorByAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);

        actionBarLayout.setBackgroundResource(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground);
    }

    private void changeStatusBarColorByAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground));
    }

    private void changeAllButtonsColorByAppTheme() {
        changeRightButtonsColorByAppTheme();
        changeExitButtonColorByAppTheme();
        changeThemeButtonColorByAppTheme();
    }

    private void changeRightButtonsColorByAppTheme() {
        for (Button button : buttonsManager.getButtons()) {
            button.changeByAppTheme();
        }
    }

    @SuppressLint("DiscouragedApi")
    private void changeExitButtonColorByAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);

        exitButton.setBackgroundResource(getResources().getIdentifier(
                "exit_" + appTheme,
                "drawable",
                getPackageName()));
    }

    @SuppressLint("DiscouragedApi")
    private void changeThemeButtonColorByAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);

        themeButton.setBackgroundResource(getResources().getIdentifier(
                "theme_" + appTheme,
                "drawable",
                getPackageName())
        );
    }

    private void changeAllViewHoldersColorByAppTheme() {
        for (int i = 0; i < adapter.getItemCount(); i++) {
            ThemeViewHolder holder = (ThemeViewHolder) themesListView.findViewHolderForAdapterPosition(i);

            if (holder != null) {
                holder.changeColorByAppTheme();
            }
        }
    }

    private void changeSearchBarColorByAppTheme() {
        searchBar.changeColorByAppTheme();
    }


    @SuppressLint("DiscouragedApi")
    private void buildExitButton() {
        exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(view -> closeApp());
    }

    private void closeApp() {
        database.close();
        this.finish();
    }

    @Override
    protected void buildSearchBar() {
        searchBar = new MainSearchBarHelper(this);
        searchBar.setOnDataRecordedListener(() -> {
            adapter.deselectSelectedViewHolder();
            buttonsManager.updateButtonsDisplay();
        });
        searchBar.build();
    }

    @Override
    protected void buildStatusAndActionBars() {
        buildStatusBar();
        buildActionBar();
    }

    private void buildStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }

    private void buildActionBar() {
        actionBarLayout = findViewById(R.id.action_bar_layout);
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
    public AbstractButtonsManager getButtonsManager() {
        return buttonsManager;
    }

    @Override
    public AbstractSearchBarHelper getSearchBar() {
        return searchBar;
    }

    @Override
    public int getSelectedViewIndex() {
        int index = -1;
        for (int i = 0; i < data.size(); i++) {
            if (((Theme) data.get(i)).id == selectedViewId) {
                index = i;
                break;
            }
        }
        if (index == -1)
            { throw new NoSuchElementException("There is no selected theme or selected theme is not visible in the data list."); }
        return index;
    }
}