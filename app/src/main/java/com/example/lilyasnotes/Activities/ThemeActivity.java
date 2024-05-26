package com.example.lilyasnotes.Activities;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Buttons.ButtonManagers.AbstractButtonsManager;
import com.example.lilyasnotes.Buttons.ButtonManagers.ThemeButtonsManager;
import com.example.lilyasnotes.Data.DTO.Data;
import com.example.lilyasnotes.Data.DTO.Note;
import com.example.lilyasnotes.Data.DTO.Theme;
import com.example.lilyasnotes.Data.ViewHolders.AbstractViewHolder;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;
import com.example.lilyasnotes.RecyclerViews.AbstractRecyclerViewAdapter;
import com.example.lilyasnotes.RecyclerViews.RecyclerViewMoveCallback;
import com.example.lilyasnotes.RecyclerViews.ThemeRecyclerViewAdapter;
import com.example.lilyasnotes.Utilities.Tools;
import com.example.lilyasnotes.Buttons.DTO.Button;
import com.example.lilyasnotes.Buttons.DTO.EditButton;
import com.example.lilyasnotes.Widgets.SearchBars.AbstractSearchBarHelper;
import com.example.lilyasnotes.Widgets.SearchBars.ThemeSearchBarHelper;

import java.util.Arrays;

public class ThemeActivity extends AbstractActivity {

    public Theme theme;

    private ThemeButtonsManager buttonsManager;
    private ThemeRecyclerViewAdapter adapter;
    private ThemeSearchBarHelper searchBar;

    private RecyclerView dataListView;
    private RelativeLayout themeTitleFrame;
    private TextView themeTitle;
    private ImageView dataListBackground;
    private ImageButton themeButton;
    private ImageButton exitButton;
    private RelativeLayout actionBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        theme = new Theme(getIntent().getIntExtra("themeId", -1), this);

        SQLiteDatabaseAdapter.printTable(null, this);
        loadDataFromDatabaseIntoDataList();

        buildRecyclerView();
        buildScrollingBackground();
        buildButtons();
        buildSearchBar();
        buildStatusAndActionBars();
        setupThemeTitle();

        changeByAppTheme();
        buttonsManager.updateButtonsDisplay();
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
    protected void buildRecyclerView() {
        dataListView = findViewById(R.id.data_list_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        dataListView.setLayoutManager(layoutManager);

        adapter = new ThemeRecyclerViewAdapter(this);
        adapter.setRecyclerView(dataListView);
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

                // Background translation
                float translationY = dataListBackground.getTranslationY() - dy * 0.5f;
                dataListBackground.setTranslationY(translationY);

                float scaleFactor = 1 - 0.2f * translationY / dataListBackground.getHeight();
                dataListBackground.setScaleX(scaleFactor);
                dataListBackground.setScaleY(scaleFactor);
            }
        });
    }

    @Override
    protected void buildButtons() {
        buildThemeButton();
        buildExitButton();

        buttonsManager = new ThemeButtonsManager(this);
        buttonsManager.addAndSetupButtonsByType(EditButton.class);
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
        changeThemeTitleColorByAppTheme();
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

    private void changeAllViewHoldersColorByAppTheme() {
        for (int i = 0; i < adapter.getItemCount(); i++) {
            AbstractViewHolder holder = (AbstractViewHolder) dataListView.findViewHolderForAdapterPosition(i);

            if (holder != null) {
                holder.changeColorByAppTheme();
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private void changeThemeTitleColorByAppTheme() {
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
        exitButton = findViewById(R.id.return_button);
        exitButton.setOnClickListener(view -> returnToPrevious());
    }

    private void returnToPrevious() {
        database.close();
        this.finish();
    }

    @Override
    protected void buildSearchBar() {
        searchBar = new ThemeSearchBarHelper(this);
        searchBar.setOnDataRecordedListener(() -> buttonsManager.updateButtonsDisplay());
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

    @SuppressLint("DiscouragedApi")
    private void setupThemeTitle() {
        themeTitleFrame = findViewById(R.id.theme_title_frame);

        themeTitle = findViewById(R.id.theme_title);
        themeTitle.setText(theme.title);
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
    public ImageView getBackground() {
        return dataListBackground;
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeByAppTheme();
    }
}
