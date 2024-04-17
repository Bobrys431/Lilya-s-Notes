package com.example.lilyasnotes.Activities;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.ButtonManagers.ButtonsManager;
import com.example.lilyasnotes.ButtonManagers.MainButtonsManager;
import com.example.lilyasnotes.Data.DTO.Theme;
import com.example.lilyasnotes.Data.ViewHolders.ThemeViewHolder;
import com.example.lilyasnotes.Widgets.Buttons.AddButton;
import com.example.lilyasnotes.Widgets.Buttons.Button;
import com.example.lilyasnotes.Widgets.Buttons.DeleteButton;
import com.example.lilyasnotes.Widgets.Buttons.RenameButton;
import com.example.lilyasnotes.Widgets.Buttons.TransitionButton;
import com.example.lilyasnotes.RecyclerViews.RecyclerViewAdapters.MainRecyclerViewAdapter;
import com.example.lilyasnotes.R;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.Widgets.SearchBars.MainSearchBarHelper;
import com.example.lilyasnotes.Widgets.SearchBars.SearchBarHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public int selectedViewId;
    public List<Theme> themes;
    public RecyclerView themesListView;
    public ButtonsManager buttonsManager;
    public MainRecyclerViewAdapter adapter;
    private SQLiteDatabase database;
    private SearchBarHelper searchBar;

    private ImageView themesListBackground;
    private ImageButton themeButton;
    private ImageButton exitButton;
    private RelativeLayout actionBarLayout;


    @SuppressLint({"NotifyDataSetChanged", "DiscouragedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = SQLiteDatabaseAdapter.getDatabase(this);
        themes = new ArrayList<>();
        selectedViewId = -1;

        SQLiteDatabaseAdapter.printTable(null, this);
        loadThemesFromDatabaseIntoThemesList();

        buildRecyclerView();
        buildScrollingBackground();
        buildButtons();
        buildSearchBar();
        setStatusAndActionBarAppTheme();

        buttonsManager.updateButtonsDisplay();
    }


    private void loadThemesFromDatabaseIntoThemesList() {
        Cursor databaseCursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEMES_THEME_ID +
                " FROM " + SQLiteDatabaseAdapter.THEMES +
                " ORDER BY " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " ASC", null);

        if (databaseCursor != null) {
            while (databaseCursor.moveToNext()) {
                themes.add(new Theme(
                        databaseCursor.getInt(databaseCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEMES_THEME_ID)),
                        this));
            }
            databaseCursor.close();
        }
    }


    private void buildRecyclerView() {
        themesListView = findViewById(R.id.themes_list_view);
        themesListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainRecyclerViewAdapter(themes, this);
        themesListView.setAdapter(adapter);
    }


    private void buildScrollingBackground() {
        themesListBackground = findViewById(R.id.themes_list_background);
        themesListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Background translation
                float translationY = themesListBackground.getTranslationY() - dy * 0.5f;
                themesListBackground.setTranslationY(translationY);

                float scaleFactor = 1 - 0.2f * translationY / themesListBackground.getHeight();
                themesListBackground.setScaleX(scaleFactor);
                themesListBackground.setScaleY(scaleFactor);
            }
        });
    }


    @SuppressLint("DiscouragedApi")
    private void buildButtons() {
        buildThemeButton();
        buildExitButton();

        buttonsManager = new MainButtonsManager(this);
        buttonsManager.addAndSetupButtonsByType(
                AddButton.class,
                DeleteButton.class,
                TransitionButton.class,
                RenameButton.class);
    }


    @SuppressLint("DiscouragedApi")
    private void buildThemeButton() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);

        themeButton = findViewById(R.id.theme_button);
        themeButton.setBackgroundResource(getResources().getIdentifier("theme_" + appTheme, "drawable", getPackageName()));
        themeButton.setOnClickListener(view -> changeAppTheme());
    }

    @SuppressLint("DiscouragedApi")
    private void changeAppTheme() {
        database.execSQL("UPDATE " + SQLiteDatabaseAdapter.ADDITIONAL_DATA + " SET " + SQLiteDatabaseAdapter.APP_THEME + " = CASE WHEN " + SQLiteDatabaseAdapter.APP_THEME + " = 'LIGHT' THEN 'DARK' ELSE 'LIGHT' END");
        changeToAppTheme();
    }

    private void changeToAppTheme() {
        changeWindowColorToAppTheme();
        changeActionBarColorToAppTheme();
        changeStatusBarColorToAppTheme();
        changeAllButtonsColorToAppTheme();
        changeAllViewHoldersColorToAppTheme();

        new Handler().postDelayed(adapter::notifyDataSetChanged, 500);
    }

    private void changeWindowColorToAppTheme() {
        Window window = getWindow();
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);

        window.setBackgroundDrawableResource(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground);
    }

    private void changeActionBarColorToAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);

        actionBarLayout.setBackgroundResource(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground);
    }

    private void changeStatusBarColorToAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground));
    }

    private void changeAllButtonsColorToAppTheme() {
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

    private void changeAllViewHoldersColorToAppTheme() {
        for (int i = 0; i < adapter.getItemCount(); i++) {
            ThemeViewHolder holder = (ThemeViewHolder) themesListView.findViewHolderForAdapterPosition(i);

            if (holder != null) {
                holder.changeViewHolderColorToAppTheme();
            }
        }
    }


    @SuppressLint("DiscouragedApi")
    private void buildExitButton() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);

        exitButton = findViewById(R.id.exit_button);
        exitButton.setBackgroundResource(getResources().getIdentifier("exit_" + appTheme, "drawable", getPackageName()));
        exitButton.setOnClickListener(view -> closeApp());
    }

    @SuppressLint("DiscouragedApi")
    private void closeApp() {
        database.close();
        this.finish();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void buildSearchBar() {
        searchBar = new MainSearchBarHelper(findViewById(R.id.search_bar), this);
        searchBar.setRecyclerViewUpdater(() -> {
            adapter.deselectSelectedViewHolder();
            adapter.notifyDataSetChanged();
        });
        searchBar.setButtonsManagerUpdater(buttonsManager::updateButtonsDisplay);
        searchBar.build();
    }


    private void setStatusAndActionBarAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);

        setStatusBarAppTheme(appTheme);
        setActionBarAppTheme(appTheme);
    }

    private void setStatusBarAppTheme(String appTheme) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(
                getResources().getColor(appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, getTheme())
        );
    }

    private void setActionBarAppTheme(String appTheme) {
        actionBarLayout = findViewById(R.id.action_bar_layout);
        actionBarLayout.setBackgroundColor(getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, getTheme()));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void reloadThemes() {
        themes.clear();
        loadThemesFromDatabaseIntoThemesList();

        adapter.deselectSelectedViewHolder();
        adapter.notifyDataSetChanged();
        buttonsManager.updateButtonsDisplay();
    }

    public SearchBarHelper getSearchBar() {
        return searchBar;
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadThemes();
        changeToAppTheme();
    }
}