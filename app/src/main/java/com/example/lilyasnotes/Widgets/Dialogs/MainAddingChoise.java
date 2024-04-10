package com.example.lilyasnotes.Widgets.Dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.example.lilyasnotes.Activities.MainActivity;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;

public class MainAddingChoise extends Dialog {

    public static final int THEME_CHOISE = 1;

    private int choiseType;
    private OnChoiseSelectedListener onChoiseSelectedListener;

    public MainAddingChoise(@NonNull MainActivity activity) {
        super(activity);
    }

    @SuppressLint("DiscouragedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_adding_choise);

        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(getContext());

        ImageButton addThemeButton = findViewById(R.id.add_theme);
        addThemeButton.setBackgroundResource(getContext().getResources().getIdentifier(
                "theme_choice_" + appTheme,
                "drawable",
                getContext().getPackageName()
        ));
        addThemeButton.setOnClickListener((view) -> {
            choiseType = THEME_CHOISE;
            onChoiseSelectedListener.onChoiseSelected();
            dismiss();
        });

        RelativeLayout basement = findViewById(R.id.basement);
        basement.setBackgroundColor(getContext().getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, getContext().getTheme()));
    }

    public void setOnChoiseSelectedListener(OnChoiseSelectedListener onChoiseSelectedListener) {
        this.onChoiseSelectedListener = onChoiseSelectedListener;
    }

    public int getChoiseType() {
        return choiseType;
    }
}
