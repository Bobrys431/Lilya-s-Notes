package com.example.lilyasnotes.Widgets.Dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.lilyasnotes.Activities.AbstractActivity;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;

public class MainAddingChoice extends Dialog {

    private int choiceType;
    private OnChoiceSelectedListener onChoiceSelectedListener;

    public MainAddingChoice(@NonNull AbstractActivity activity) {
        super(activity);
    }

    @SuppressLint("DiscouragedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_adding_choice);

        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(getContext());

        ImageButton addThemeButton = findViewById(R.id.add_theme);
        addThemeButton.setBackgroundResource(getContext().getResources().getIdentifier(
                "theme_choice_" + appTheme,
                "drawable",
                getContext().getPackageName()
        ));
        addThemeButton.setOnClickListener((view) -> {
            choiceType = AbstractActivity.THEME_TYPE;
            onChoiceSelectedListener.onChoiseSelected();
            dismiss();
        });

        LinearLayout basement = findViewById(R.id.basement);
        basement.setBackgroundColor(getContext().getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, getContext().getTheme()));
    }

    public void setOnChoiceSelectedListener(OnChoiceSelectedListener onChoiceSelectedListener) {
        this.onChoiceSelectedListener = onChoiceSelectedListener;
    }

    public int getChoiceType() {
        return choiceType;
    }
}
