package com.example.lilyasnotes.Widgets.Dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.example.lilyasnotes.Activities.ThemeActivity;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;

public class ThemeAddingChoice extends Dialog {

    public static final int THEME_CHOICE = 1;
    public static final int NOTE_CHOICE = 2;

    private int choiceType;
    private OnChoiceSelectedListener onChoiceSelectedListener;

    public ThemeAddingChoice(@NonNull ThemeActivity activity) {
        super(activity);
    }

    @SuppressLint("DiscouragedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theme_adding_choice);

        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(getContext());

        ImageButton addThemeButton = findViewById(R.id.add_theme);
        addThemeButton.setBackgroundResource(getContext().getResources().getIdentifier(
                "theme_choice_" + appTheme,
                "drawable",
                getContext().getPackageName()
        ));
        addThemeButton.setOnClickListener((view) -> {
            choiceType = THEME_CHOICE;
            onChoiceSelectedListener.onChoiseSelected();
            dismiss();
        });

        RelativeLayout themeFrame = findViewById(R.id.theme_frame);
        themeFrame.setBackgroundColor(getContext().getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackgroundAddition :
                        R.color.darkThemeBackgroundAddition, getContext().getTheme()));

        ImageButton addNoteButton = findViewById(R.id.add_note);
        addNoteButton.setBackgroundResource(getContext().getResources().getIdentifier(
                "note_choice_" + appTheme,
                "drawable",
                getContext().getPackageName()
        ));
        addNoteButton.setOnClickListener((view) -> {
            choiceType = NOTE_CHOICE;
            onChoiceSelectedListener.onChoiseSelected();
            dismiss();
        });

        RelativeLayout noteFrame = findViewById(R.id.note_frame);
        noteFrame.setBackgroundColor(getContext().getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackgroundAddition :
                        R.color.darkThemeBackgroundAddition, getContext().getTheme()));

        RelativeLayout basement = findViewById(R.id.basement);
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
