package com.example.lilyasnotes;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class Main extends AppCompatActivity implements ConfirmDialog.ConfirmDialogListener
{
    static List<Theme> themes;
    SQLiteDatabase database;

    RecyclerView themesListView;
    MainRecyclerViewAdapter adapter;
    ImageView themesListBackground;
    NestedScrollView backgroundScrollView;
    int selectedViewPosition;

    ImageButton addButton;
    GifImageView addSplash;
    ImageButton deleteButton;
    GifImageView deleteSplash;
    ImageButton themeButton;
    GifImageView themeSplash;
    ImageButton exitButton;
    GifImageView exitSplash;

    EditText searchBar;
    Map<Integer, Boolean> hiddenThemes;
    boolean isSearching;

    List<Decoration> decorations;
    RelativeLayout actionBarLayout;


    @SuppressLint({"NotifyDataSetChanged", "DiscouragedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = SQLiteDatabaseAdapter.getDatabase(this);
        themes = new ArrayList<>();
        selectedViewPosition = 0;
        decorations = new ArrayList<>();
        isSearching = false;
        hiddenThemes = new HashMap<>();

        SQLiteDatabaseAdapter.printTable(null, this);


        // Loading data from DB
        Cursor databaseCursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEMES_THEME_ID +
                " FROM " + SQLiteDatabaseAdapter.THEMES +
                " ORDER BY " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " ASC", null);

        if (databaseCursor != null)
        {
            while (databaseCursor.moveToNext())
            {
                themes.add(new Theme(
                        databaseCursor.getInt(databaseCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_ID)),
                        this));
            }
            databaseCursor.close();
        }

        final String appTheme;


        // Set StatusBar background theme
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (SQLiteDatabaseAdapter.getCurrentAppTheme(this) != null) {
                appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);
                window.setStatusBarColor(
                        getResources().getColor(appTheme.equals("light") ?
                                R.color.lightThemeBackground :
                                R.color.darkThemeBackground, getTheme())
                );
            } else
            {
                window.setStatusBarColor(getResources().getColor(R.color.lightThemeBackground, getTheme()));
                database.execSQL("INSERT INTO " + SQLiteDatabaseAdapter.ADDITIONAL_DATA + "(" + SQLiteDatabaseAdapter.APP_THEME + ") VALUES ('LIGHT')");
                appTheme = "light";
            }
        }


        // RecyclerView organizing
        themesListView = findViewById(R.id.themes_list_view);
        themesListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainRecyclerViewAdapter(themes, themesListView);
        themesListView.setAdapter(adapter);

        themesListBackground = findViewById(R.id.themes_list_background);
        themesListView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            int lastSelectedView = 0;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                // Background translation
                float translationY = themesListBackground.getTranslationY() - dy * 0.5f;
                themesListBackground.setTranslationY(translationY);

                float scaleFactor = 1 - 0.2f * translationY / themesListBackground.getHeight();
                themesListBackground.setScaleX(scaleFactor);
                themesListBackground.setScaleY(scaleFactor);

                // Elements cursor
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) themesListView.getLayoutManager();
                if (linearLayoutManager != null)
                {
                    selectedViewPosition =
                            (linearLayoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItemCount() - 1) ?
                                    linearLayoutManager.findLastCompletelyVisibleItemPosition() :
                                    linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                }

                if (linearLayoutManager != null)
                {
                    for (int i = 0; i < adapter.getItemCount(); i++)
                    {
                        Theme.ThemeViewHolder themeViewHolder = (Theme.ThemeViewHolder) themesListView.findViewHolderForAdapterPosition(i);
                        View view = linearLayoutManager.findViewByPosition(i);
                        if (themeViewHolder != null && view != null)
                        {
                            float targetAlpha = (i == selectedViewPosition) ? 0.85f : 0.35f;
                            ObjectAnimator.ofFloat(view, "alpha", view.getAlpha(), targetAlpha).setDuration(400).start();

                            float targetScale = (i == selectedViewPosition) ? 1.0f : 0.7f;
                            ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), targetScale).setDuration(400).start();
                            ObjectAnimator.ofFloat(view, "scaleY", view.getScaleY(), targetScale).setDuration(400).start();

                            if (!isSearching)
                            {
                                float targetTranslation = (i == selectedViewPosition) ? 0f : -150f;
                                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(themeViewHolder.translationFrame, "translationX", themeViewHolder.translationFrame.getTranslationX(), targetTranslation);
                                objectAnimator.setInterpolator(new DecelerateInterpolator());
                                objectAnimator.setDuration(200).start();
                            }

                            if (i == selectedViewPosition && lastSelectedView != selectedViewPosition)
                            {
                                int qQ = selectedViewPosition;
                                new Handler().postDelayed(() -> {
                                    if (qQ == selectedViewPosition)
                                    {
                                        Animation animation = AnimationUtils.loadAnimation(Main.this, R.anim.shake);
                                        view.startAnimation(animation);
                                    }
                                }, 350);
                            }

                            themeViewHolder.isSelected = i == selectedViewPosition;
                        }
                    }
                    lastSelectedView = selectedViewPosition;
                }
            }
        });


        // Buttons
        addButton = findViewById(R.id.add_button);
        deleteButton = findViewById(R.id.delete_button);
        themeButton = findViewById(R.id.theme_button);
        exitButton = findViewById(R.id.exit_button);

        addSplash = findViewById(R.id.add_splash);
        deleteSplash = findViewById(R.id.delete_splash);
        themeSplash = findViewById(R.id.theme_splash);
        exitSplash = findViewById(R.id.exit_splash);

        final int duration;
        try
        {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.splash_light);
            duration = gifDrawable.getDuration();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }


        addButton.setBackgroundResource(getResources().getIdentifier("add_" + appTheme, "drawable", getPackageName()));
        addButton.setOnClickListener(view ->
        {
            Theme.ThemeViewDialog themeViewDialog = new Theme.ThemeViewDialog(-1);
            themeViewDialog.setOnDialogClosedListener(result ->
            {

                database.execSQL("INSERT INTO " + SQLiteDatabaseAdapter.THEME + "(" + SQLiteDatabaseAdapter.THEME_TITLE + ")" +
                        " VALUES (?)", new Object[]{result});

                String[] selectionArgs = {result};
                Cursor newThemeCursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_ID +
                        " FROM " + SQLiteDatabaseAdapter.THEME +
                        " WHERE " + SQLiteDatabaseAdapter.THEME_TITLE + " = ?" +
                        " ORDER BY " + SQLiteDatabaseAdapter.THEME_ID + " DESC" +
                        " LIMIT 1", selectionArgs);

                Cursor maxIndexCursor = database.rawQuery("SELECT MAX(" + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + ") AS max_theme_index" +
                        " FROM " + SQLiteDatabaseAdapter.THEMES, null);

                if (newThemeCursor != null && newThemeCursor.moveToFirst() && maxIndexCursor != null && maxIndexCursor.moveToFirst())
                {
                    database.execSQL("INSERT INTO " + SQLiteDatabaseAdapter.THEMES + "(" + SQLiteDatabaseAdapter.THEMES_THEME_ID + ", " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX +")" +
                            " VALUES ('" + newThemeCursor.getInt(newThemeCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_ID)) + "', " + maxIndexCursor.getInt(maxIndexCursor.getColumnIndexOrThrow("max_theme_index")) + " + 1)");

                    hiddenThemes.put(maxIndexCursor.getInt(maxIndexCursor.getColumnIndexOrThrow("max_theme_index")), false);
                    themes.add(new Theme(
                            newThemeCursor.getInt(newThemeCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_ID)),
                            this));

                    newThemeCursor.close();
                    maxIndexCursor.close();
                }
                adapter.notifyDataSetChanged();
            });
            themeViewDialog.show(getSupportFragmentManager(), "ThemeViewDialog");

            final String changedAppTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);
            if (changedAppTheme != null)
                addSplash.setImageResource(getResources().getIdentifier("splash_" + changedAppTheme, "drawable", getPackageName()));
            new Handler().postDelayed(() -> addSplash.setImageDrawable(null), duration);
        });
        addSplash.setImageDrawable(null);

        deleteButton.setBackgroundResource(getResources().getIdentifier("delete_" + appTheme, "drawable", getPackageName()));
        deleteButton.setOnClickListener(view ->
        {
            Cursor themeToDelete = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_TITLE +
                    " FROM " + SQLiteDatabaseAdapter.THEME +
                    " WHERE " + SQLiteDatabaseAdapter.THEME_ID + " = " + themes.get(selectedViewPosition).id, null);
            if (themeToDelete != null && themeToDelete.moveToFirst())
            {
                ConfirmDialog confirmDialog = new ConfirmDialog("Ти точно хочеш видалити тему:\n" + themeToDelete.getString(themeToDelete.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_TITLE)));
                confirmDialog.show(getSupportFragmentManager(), "Confirm to delete Theme dialog");
                themeToDelete.close();
            }

            final String changedAppTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);
            if (changedAppTheme != null)
                deleteSplash.setImageResource(getResources().getIdentifier("splash_" + changedAppTheme, "drawable", getPackageName()));
            new Handler().postDelayed(() -> deleteSplash.setImageDrawable(null), duration);
        });
        deleteSplash.setImageDrawable(null);

        themeButton.setBackgroundResource(getResources().getIdentifier("theme_" + appTheme, "drawable", getPackageName()));
        themeButton.setOnClickListener(view ->
        {
            database.execSQL("UPDATE " + SQLiteDatabaseAdapter.ADDITIONAL_DATA + " SET " + SQLiteDatabaseAdapter.APP_THEME + " = CASE WHEN " + SQLiteDatabaseAdapter.APP_THEME + " = 'LIGHT' THEN 'DARK' ELSE 'LIGHT' END");

            Handler handler = new Handler();
            final String changedAppTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);
            if (changedAppTheme != null)
            {
                int currentTheme;
                int notCurrentTheme;
                currentTheme = getResources().getColor(
                        changedAppTheme.equals("light") ?
                                R.color.lightThemeBackground :
                                R.color.darkThemeBackground, getTheme());
                notCurrentTheme = getResources().getColor(
                        changedAppTheme.equals("light") ?
                                R.color.darkThemeBackground :
                                R.color.lightThemeBackground, getTheme());

                if (currentTheme != 0 && notCurrentTheme != 0)
                {
                    handler.postDelayed(() -> getWindow().setBackgroundDrawableResource(
                            changedAppTheme.equals("light") ?
                                    R.color.lightThemeBackground :
                                    R.color.darkThemeBackground), 200);

                    ObjectAnimator animator = ObjectAnimator.ofArgb(actionBarLayout, "backgroundColor", notCurrentTheme, currentTheme);
                    animator.setDuration(500);
                    animator.start();
                    handler.postDelayed(() -> actionBarLayout.setBackgroundColor(currentTheme), 500);

                    animator = ObjectAnimator.ofArgb(getWindow(), "statusBarColor", notCurrentTheme, currentTheme);
                    animator.setDuration(500);
                    animator.start();
                    handler.postDelayed(() ->
                    {
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(currentTheme);
                    }, 500);

                    handler.postDelayed(() ->
                    {
                        addButton.setBackgroundResource(getResources().getIdentifier("add_" + changedAppTheme, "drawable", getPackageName()));
                        deleteButton.setBackgroundResource(getResources().getIdentifier("delete_" + changedAppTheme, "drawable", getPackageName()));
                        themeButton.setBackgroundResource(getResources().getIdentifier("theme_" + changedAppTheme, "drawable", getPackageName()));
                        exitButton.setBackgroundResource(getResources().getIdentifier("exit_" + changedAppTheme, "drawable", getPackageName()));
                    }, 200);
                    handler.postDelayed(() ->
                    {
                        for (int i = 0; i < decorations.size(); i++)
                            decorations.get(i).getImageView().setImageResource(getResources().getIdentifier("decoration_" + decorations.get(i).getImageType() + "_" + changedAppTheme, "drawable", getPackageName()));
                    }, 200);

                    for (int i = 0; i < adapter.getItemCount(); i++)
                    {
                        Theme.ThemeViewHolder themeViewHolder = (Theme.ThemeViewHolder) themesListView.findViewHolderForAdapterPosition(i);

                        if (themeViewHolder != null)
                        {
                            ObjectAnimator.ofArgb(themeViewHolder.titleFrame, "backgroundColor", notCurrentTheme, currentTheme).setDuration(500).start();
                            handler.postDelayed(() -> themeViewHolder.titleFrame.setBackgroundColor(currentTheme), 500);

                            ObjectAnimator.ofArgb(themeViewHolder.dataFrame, "backgroundColor", notCurrentTheme, currentTheme).setDuration(500).start();
                            handler.postDelayed(() -> themeViewHolder.dataFrame.setBackgroundColor(currentTheme), 500);

                            int startColor = getResources().getColor(
                                    changedAppTheme.equals("light") ?
                                            R.color.white :
                                            R.color.black, getTheme());
                            int endColor = getResources().getColor(
                                    changedAppTheme.equals("light") ?
                                            R.color.black :
                                            R.color.white, getTheme());
                            ValueAnimator textAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
                            textAnimator.setDuration(500);

                            textAnimator.addUpdateListener(valueAnimator -> themeViewHolder.title.setTextColor((int) textAnimator.getAnimatedValue()));
                            textAnimator.start();
                            handler.postDelayed(() -> themeViewHolder.title.setTextColor(startColor), 500);

                            handler.postDelayed(() ->
                            {
                                for (int j = 0; j < themeViewHolder.decorations.size(); j++)
                                    themeViewHolder.decorations.get(j).getImageView().setImageResource(getResources().getIdentifier("decoration_" + themeViewHolder.decorations.get(j).getImageType() + "_" + changedAppTheme, "drawable", getPackageName()));
                            }, 200);

                            ObjectAnimator.ofArgb(themeViewHolder.translationFrame, "backgroundColor", notCurrentTheme, currentTheme).setDuration(500).start();
                            handler.postDelayed(() -> themeViewHolder.translationFrame.setBackgroundColor(currentTheme), 500);

                            handler.postDelayed(() ->
                            {
                                themeViewHolder.translationUp.setBackgroundResource(
                                        changedAppTheme.equals("light") ?
                                                R.drawable.translate_light :
                                                R.drawable.translate_dark);
                                themeViewHolder.translationDown.setBackgroundResource(
                                        changedAppTheme.equals("light") ?
                                                R.drawable.translate_light :
                                                R.drawable.translate_dark);
                            }, 200);
                        }
                    }
                    handler.postDelayed(adapter::notifyDataSetChanged, 500);
                }

                themeSplash.setImageResource(getResources().getIdentifier("splash_" + changedAppTheme, "drawable", getPackageName()));
                new Handler().postDelayed(() -> themeSplash.setImageDrawable(null), duration);
            }
        });
        themeSplash.setImageDrawable(null);

        exitButton.setBackgroundResource(getResources().getIdentifier("exit_" + appTheme, "drawable", getPackageName()));
        exitButton.setOnClickListener(view ->
        {
            final String changedAppTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(this);
            if (changedAppTheme != null)
                exitSplash.setImageResource(getResources().getIdentifier("splash_" + changedAppTheme, "drawable", getPackageName()));
            new Handler().postDelayed(() -> exitSplash.setImageDrawable(null), duration);

            database.close();
            this.finish();
        });
        exitSplash.setImageDrawable(null);


        Cursor hiddenCursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX +
                " FROM " + SQLiteDatabaseAdapter.THEMES +
                " ORDER BY " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " ASC", null);
        if (hiddenCursor != null)
        {
            while (hiddenCursor.moveToNext())
                hiddenThemes.put(hiddenCursor.getInt(hiddenCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEMES_THEME_INDEX)), false);
            hiddenCursor.close();
        }

        backgroundScrollView = findViewById(R.id.background_scroll_view);
        searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                // **NOTHING**
            }

            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                System.out.println(charSequence + "   " + charSequence.length());

                Cursor indexes = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX +
                        " FROM " + SQLiteDatabaseAdapter.THEMES +
                        " ORDER BY " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " ASC", null);
                if (indexes != null)
                {
                    while (indexes.moveToNext())
                    {
                        if (hiddenThemes.get(indexes.getInt(indexes.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEMES_THEME_INDEX))) == null)
                        {
                            hiddenThemes.remove(indexes.getInt(indexes.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEMES_THEME_INDEX)));
                            hiddenThemes.put(indexes.getInt(indexes.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEMES_THEME_INDEX)), false);
                        }
                    }
                    indexes.close();
                }

                // If all the text was erased
                if (charSequence.length() == 0)
                {
                    Cursor cursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX +
                            " FROM " + SQLiteDatabaseAdapter.THEMES +
                            " ORDER BY " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " ASC", null);
                    if (cursor != null)
                    {
                        while (cursor.moveToNext())
                        {
                            hiddenThemes.replace(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEMES_THEME_INDEX)), false);
                        }
                        cursor.close();
                    }
                    isSearching = false;

                } else
                {
                    // Getting data from DB and then compare it with searchBar value
                    Cursor cursor = database.rawQuery("SELECT " +
                            SQLiteDatabaseAdapter.THEME_ID + ", " +
                            SQLiteDatabaseAdapter.THEMES_THEME_INDEX + ", " +
                            SQLiteDatabaseAdapter.THEME_TITLE +
                            " FROM " + SQLiteDatabaseAdapter.THEME +
                            " INNER JOIN " + SQLiteDatabaseAdapter.THEMES + " USING(" + SQLiteDatabaseAdapter.THEME_ID + ")" +
                            " ORDER BY " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " ASC", null);
                    if (cursor != null)
                    {
                        while (cursor.moveToNext())
                        {
                            hiddenThemes.replace(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEMES_THEME_INDEX)), (new StringBuilder(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_TITLE))).indexOf(charSequence.toString())) == -1);

                            Cursor themeTextCursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_TEXT_NOTE +
                                    " FROM " + SQLiteDatabaseAdapter.THEME_TEXT +
                                    " WHERE " + SQLiteDatabaseAdapter.THEME_TEXT_THEME_ID + " = " + cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_ID)), null);
                            if (themeTextCursor != null)
                            {
                                while (themeTextCursor.moveToNext())
                                {
                                    if (new StringBuilder(themeTextCursor.getString(themeTextCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_TEXT_NOTE))).indexOf(charSequence.toString()) != -1)
                                    {
                                        hiddenThemes.replace(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEMES_THEME_INDEX)), false);
                                        break;
                                    }
                                }
                                themeTextCursor.close();
                            }

                            Cursor themeIntoCursor = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_TITLE +
                                    " FROM " + SQLiteDatabaseAdapter.THEME +
                                    " WHERE " + SQLiteDatabaseAdapter.THEME_ID + " = (SELECT " + SQLiteDatabaseAdapter.THEME_INTO_THEME_IN_ID +
                                    " FROM " + SQLiteDatabaseAdapter.THEME_INTO +
                                    " WHERE " + SQLiteDatabaseAdapter.THEME_INTO_THEME_ID + " = " + cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_ID)) + ")", null);
                            if (themeIntoCursor != null)
                            {
                                while (themeIntoCursor.moveToNext())
                                {
                                    if (new StringBuilder(themeIntoCursor.getString(themeIntoCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_TITLE))).indexOf(charSequence.toString()) != -1) {
                                        hiddenThemes.replace(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEMES_THEME_INDEX)), false);
                                        break;
                                    }
                                }
                                themeIntoCursor.close();
                            }
                        }
                        cursor.close();
                    }

                    isSearching = true;
                    for (int j = 0; j < adapter.getItemCount(); j++)
                    {
                        Theme.ThemeViewHolder themeViewHolder = (Theme.ThemeViewHolder) themesListView.findViewHolderForAdapterPosition(j);
                        if (themeViewHolder != null)
                            themeViewHolder.translationFrame.setTranslationX(-150f);
                    }
                }
                Cursor cursor = database.rawQuery("SELECT " +
                        SQLiteDatabaseAdapter.THEMES_THEME_ID + ", " +
                        SQLiteDatabaseAdapter.THEMES_THEME_INDEX +
                        " FROM " + SQLiteDatabaseAdapter.THEMES +
                        " ORDER BY " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " ASC", null, null);
                if (cursor != null)
                {
                    // themesListBackground.setY(themesListView.getTop() - 200);
                    themes.clear();
                    while (cursor.moveToNext())
                        if (Boolean.FALSE.equals(hiddenThemes.get(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEMES_THEME_INDEX)))))
                            themes.add(new Theme(
                                    cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_ID)),
                                    getApplicationContext()));
                    adapter.notifyDataSetChanged();
                    cursor.close();
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                // **NOTHING**
            }
        });


        // Add decorations on Action Bar layout
        actionBarLayout = findViewById(R.id.action_bar_layout);
        actionBarLayout.setBackgroundColor(getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, getTheme()));

        Random random = new Random();
        Context context = this;
        Handler handler = new Handler();
        handler.postDelayed(() ->
        {
            for (int i = 0; i < 15; i++)
            {
                ImageView decoration = new ImageView(context);

                int leftM = actionBarLayout.getWidth() - random.nextInt(actionBarLayout.getWidth());
                int topM = actionBarLayout.getHeight() - random.nextInt(actionBarLayout.getHeight());
                int decorationType = random.nextInt(8) + 1;

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.width = 120;
                params.height = 120;
                params.leftMargin = leftM;
                params.topMargin = topM;
                decoration.setZ(0);
                decoration.setRotation(random.nextInt(360));
                decoration.setAlpha(0.75f);

                decoration.setImageResource(getResources().getIdentifier(
                        "decoration_" + decorationType + "_" + appTheme,
                        "drawable",
                        getPackageName()));

                actionBarLayout.addView(decoration, params);

                decoration.setId(View.generateViewId());
                decorations.add(new Decoration(decoration, decorationType));
            }
        }, 100);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void isConfirmed(boolean isConfirmed)
    {
        if (isConfirmed)
        {
            Cursor indexOfThemeToDelete = database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX +
                    " FROM " + SQLiteDatabaseAdapter.THEMES +
                    " WHERE " + SQLiteDatabaseAdapter.THEMES_THEME_ID + " = " + themes.get(selectedViewPosition).id, null);

            SQLiteDatabaseAdapter.deleteTheme(themes.get(selectedViewPosition).id, this, true);

            themes.remove(selectedViewPosition);
            if (indexOfThemeToDelete != null && indexOfThemeToDelete.moveToFirst())
            {
                hiddenThemes.remove(indexOfThemeToDelete.getInt(indexOfThemeToDelete.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEMES_THEME_INDEX)));
                indexOfThemeToDelete.close();
            }

            adapter.notifyDataSetChanged();
        }
    }
}