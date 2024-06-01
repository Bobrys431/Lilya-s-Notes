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
import com.example.lilyasnotes.DatabaseManagement.NoteManager;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;

public class NoteViewHolder extends AbstractViewHolder {

    private int id;

    private final RelativeLayout basement;
    private final TextView title;
    private final RelativeLayout titleFrame;
    private final TextView note;
    private final RelativeLayout noteFrame;

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

    private void setupBasement() {
        basement.setOnClickListener(view -> {

            if (activity.eraseMode) {
                deleteNote();
                return;
            }

            openNote();
        });
    }

    private void deleteNote() {
        activity.getUndoEraseWidget().activate(activity.data.get(getAdapterPosition()));

        activity.data.remove(getAdapterPosition());
        activity.getAdapter().notifyItemRemoved(getAdapterPosition());
    }

    private void openNote() {
        // TODO  *******OPEN**NOTE**FUNCTIONAL******   D:<<    <----
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
        title.setTextColor(activity.getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeActiveColor :
                        R.color.darkThemeActiveColor));
    }

    @SuppressLint("DiscouragedApi")
    private void changeTitleFrameColor(String appTheme) {
        titleFrame.setBackgroundResource(activity.getResources().getIdentifier(
                "note_frame_" + appTheme,
                "drawable",
                activity.getPackageName()));
    }

    private void changeNoteColor(String appTheme) {
        note.setTextColor(activity.getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeActiveColor :
                        R.color.darkThemeActiveColor));
    }

    private void changeNoteFrameColor(String appTheme) {
        noteFrame.setBackgroundColor(activity.getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground));
    }
}