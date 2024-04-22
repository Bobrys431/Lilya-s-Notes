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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Buttons.ButtonManagers.ButtonsManager;
import com.example.lilyasnotes.Buttons.ButtonManagers.ThemeButtonsManager;
import com.example.lilyasnotes.Data.DTO.Data;
import com.example.lilyasnotes.Data.DTO.Note;
import com.example.lilyasnotes.Data.DTO.Theme;
import com.example.lilyasnotes.Data.ViewHolders.DataViewHolder;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;
import com.example.lilyasnotes.RecyclerViews.RecyclerViewAdapters.ThemeRecyclerViewAdapter;
import com.example.lilyasnotes.Utilities.Tools;
import com.example.lilyasnotes.Buttons.DTO.AddButton;
import com.example.lilyasnotes.Buttons.DTO.Button;
import com.example.lilyasnotes.Buttons.DTO.DeleteButton;
import com.example.lilyasnotes.Buttons.DTO.EditButton;
import com.example.lilyasnotes.Buttons.DTO.TransitionButton;
import com.example.lilyasnotes.Widgets.SearchBars.SearchBarHelper;
import com.example.lilyasnotes.Widgets.SearchBars.ThemeSearchBarHelper;

import java.util.ArrayList;
import java.util.Arrays;
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

    private ImageView dataListBackground;
    private ImageButton themeButton;
    private ImageButton exitButton;
    private RelativeLayout actionBarLayout;
    private RelativeLayout themeTitleFrame;
    private TextView themeTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        theme = new Theme(getIntent().getIntExtra("themeId", -1), this);

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
        setStatusAndActionBarAppTheme();
        setupThemeTitle();
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

    private void buildRecyclerView() {
        dataListView = findViewById(R.id.data_list_view);
        dataListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ThemeRecyclerViewAdapter(data, this);
        dataListView.setAdapter(adapter);
    }

    private void buildScrollingBackground() {
        dataListBackground = findViewById(R.id.data_list_background);
        dataListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Background translation
                float translationY = dataListBackground.getTranslationY() - dy * 0.5f;
                dataListBackground.setTranslationY(translationY);

                float scaleFactor = 1 - 0.2f * translationY / dataListBackground.getHeight();
                dataListBackground.setScaleX(scaleFactor);
                dataListBackground.setScaleY(scaleFactor);
            }
        });
    }

    private void buildButtons() {
        buildThemeButton();
        buildExitButton();

        buttonsManager = new ThemeButtonsManager(this);
        buttonsManager.addAndSetupButtonsByType(
                AddButton.class,
                DeleteButton.class,
                TransitionButton.class,
                EditButton.class);
    }

    @SuppressLint("DiscouragedApi")
    private void buildThemeButton() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);

        themeButton = findViewById(R.id.theme_button);
        themeButton.setBackgroundResource(getResources().getIdentifier("theme_" + appTheme, "drawable", getPackageName()));
        themeButton.setOnClickListener(view -> changeAppTheme());
    }

    private void changeAppTheme() {
        database.execSQL("UPDATE " + SQLiteDatabaseAdapter.ADDITIONAL_DATA + " SET " + SQLiteDatabaseAdapter.APP_THEME + " = CASE WHEN " + SQLiteDatabaseAdapter.APP_THEME + " = 'LIGHT' THEN 'DARK' ELSE 'LIGHT' END");

        changeWindowColorToAppTheme();
        changeActionBarColorToAppTheme();
        changeStatusBarColorToAppTheme();
        changeAllButtonsColorToAppTheme();
        changeAllViewHoldersColorToAppTheme();
        changeThemeTitleColorToAppTheme();

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
                "return_" + appTheme,
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
            DataViewHolder holder = (DataViewHolder) dataListView.findViewHolderForAdapterPosition(i);

            if (holder != null) {
                holder.changeViewHolderColorToAppTheme();
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private void changeThemeTitleColorToAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);

        themeTitleFrame.setBackgroundResource(getResources().getIdentifier(
                "theme_title_" + appTheme,
                "drawable",
                getPackageName()
        ));

        themeTitle.setTextColor(getResources().getColor(
                appTheme.equals("light") ?
                        R.color.black :
                        R.color.white, getTheme()));
    }

    @SuppressLint("DiscouragedApi")
    private void buildExitButton() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);

        exitButton = findViewById(R.id.return_button);
        exitButton.setBackgroundResource(getResources().getIdentifier("return_" + appTheme, "drawable", getPackageName()));
        exitButton.setOnClickListener(view -> returnToPrevious());
    }

    private void returnToPrevious() {
        database.close();
        this.finish();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void buildSearchBar() {
        searchBar = new ThemeSearchBarHelper(findViewById(R.id.search_bar), this);
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

    @SuppressLint("DiscouragedApi")
    private void setupThemeTitle() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);

        themeTitleFrame = findViewById(R.id.theme_title_frame);
        themeTitleFrame.setBackgroundResource(getResources().getIdentifier(
                "theme_title_" + appTheme,
                "drawable",
                getPackageName()
        ));

        themeTitle = findViewById(R.id.theme_title);
        themeTitle.setText(theme.title);
        themeTitle.setTextColor(getResources().getColor(
                appTheme.equals("light") ?
                        R.color.black :
                        R.color.white, getTheme()));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void reloadData() {
        data.clear();
        loadDataFromDatabaseIntoDataList();

        adapter.deselectSelectedViewHolder();
        adapter.notifyDataSetChanged();
        buttonsManager.updateButtonsDisplay();
    }

    public SearchBarHelper getSearchBar() {
        return searchBar;
    }
}
