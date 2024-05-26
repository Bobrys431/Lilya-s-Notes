package com.example.lilyasnotes.Data.ViewHolders;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.example.lilyasnotes.Activities.AbstractActivity;
import com.example.lilyasnotes.Database.NoteManager;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;

public class NoteViewHolder extends AbstractViewHolder {

    private int id;

    public RelativeLayout basement;
    public TextView title;
    public RelativeLayout titleFrame;
    public TextView note;
    public RelativeLayout noteFrame;

    public NoteViewHolder(@NonNull View itemView) {
        super(itemView);

        basement = itemView.findViewById(R.id.basement);
        title = itemView.findViewById(R.id.title);
        titleFrame = itemView.findViewById(R.id.title_frame);
        note = itemView.findViewById(R.id.note);
        noteFrame = itemView.findViewById(R.id.note_frame);
    }

    @Override
    public void setup(int id, AbstractActivity activity) {
        final String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);
        assert appTheme != null : "NoteViewHolder: appTheme = null";

        this.id = id;
        this.activity = activity;

        setupBasement();
        setupTitle();
        setupNote();

        changeColorByAppTheme();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupBasement() {
        basement.setOnClickListener(view -> {

        });
    }

    private void setupTitle() {
        title.setText(NoteManager.getTitle(id));
        title.setTypeface(ResourcesCompat.getFont(activity, R.font.advent_pro_bold));
    }

    private void setupNote() {
        note.setText(NoteManager.getText(id));
        note.setTypeface(ResourcesCompat.getFont(activity, R.font.advent_pro_bold));

        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { /* **NOTHING** */ }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                NoteManager.setText(id, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) { /* **NOTHING** */ }
        });
    }

    public void changeColorByAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);

        changeTitleColor(appTheme);
        changeTitleFrameColor(appTheme);
        changeNoteColor(appTheme);
        changeNoteFrameColor(appTheme);
    }

    private void changeTitleColor(String appTheme) {
        title.setTextColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeActiveColor :
                        R.color.darkThemeActiveColor, activity.getTheme()));
    }

    @SuppressLint("DiscouragedApi")
    private void changeTitleFrameColor(String appTheme) {
        titleFrame.setBackgroundResource(activity.getResources().getIdentifier(
                "note_frame_" + appTheme,
                "drawable",
                activity.getPackageName()));
    }

    private void changeNoteColor(String appTheme) {
        note.setTextColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeActiveColor :
                        R.color.darkThemeActiveColor, activity.getTheme()));
    }

    private void changeNoteFrameColor(String appTheme) {
        noteFrame.setBackgroundColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, activity.getTheme()));
    }
}