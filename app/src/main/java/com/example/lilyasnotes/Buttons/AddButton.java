package com.example.lilyasnotes.Buttons;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;

import com.example.lilyasnotes.Activities.AbstractActivity;
import com.example.lilyasnotes.Activities.MainActivity;
import com.example.lilyasnotes.Activities.ThemeActivity;
import com.example.lilyasnotes.Database.NoteManager;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.Database.ThemeIntoManager;
import com.example.lilyasnotes.Database.ThemeManager;
import com.example.lilyasnotes.Database.ThemeNoteManager;
import com.example.lilyasnotes.Database.ThemesManager;
import com.example.lilyasnotes.Widgets.Dialogs.MainAddingChoice;
import com.example.lilyasnotes.Widgets.Dialogs.ThemeAddingChoice;
import com.example.lilyasnotes.Widgets.WidgetEditors.NoteWidgetEditor;
import com.example.lilyasnotes.Widgets.WidgetEditors.ThemeWidgetEditor;

public class AddButton {

    AbstractActivity activity;
    ImageButton button;

    public boolean isBlocked;

    public AddButton(ImageButton button) {
        this.button = button;
    }

    public void setup(AbstractActivity activity, View.OnClickListener onClickListener) {
        this.activity = activity;

        button.setOnClickListener((view) -> {
            if (!isBlocked) {
                onClickListener.onClick(button);

                new Handler().postDelayed(() -> {
                    if (activity instanceof ThemeActivity)
                    { requestInsertionToTheme(); }

                    else if (activity instanceof MainActivity)
                    { requestInsertionToThemes(); }
                }, 200);
            }
        });
    }

    private void requestInsertionToTheme() {
        ThemeAddingChoice tac = new ThemeAddingChoice(activity);
        tac.setOnChoiceSelectedListener(() -> {
            tac.dismiss();

            int choiceType = tac.getChoiceType();
            if (choiceType == AbstractActivity.THEME_TYPE)
                { addNewTheme(); }
            else if (choiceType == AbstractActivity.NOTE_TYPE)
                { addNewNote(); }
        });
        tac.show();
    }

    private void requestInsertionToThemes() {
        MainAddingChoice mac = new MainAddingChoice(activity);
        mac.setOnChoiceSelectedListener(() -> {
            mac.dismiss();

            int choiceType = mac.getChoiceType();
            if (choiceType == AbstractActivity.THEME_TYPE)
                { addNewTheme(); }
        });
        mac.show();
    }

    private void addNewTheme() {
        ThemeWidgetEditor themeEditor = new ThemeWidgetEditor(activity);

        themeEditor.setOnDismissListener((dialogInterface) -> {
            ThemeManager.addNewTheme(themeEditor.getTitle());

            if (activity instanceof MainActivity)
                { ThemesManager.addConnection(ThemeManager.getLastThemeId()); }
            else if (activity instanceof ThemeActivity)
                { ThemeIntoManager.addConnection(((ThemeActivity) activity).theme.id, ThemeManager.getLastThemeId()); }

            updateAdapterSequence();
        });

        themeEditor.show();
    }

    private void addNewNote() {
        NoteWidgetEditor noteEditor = new NoteWidgetEditor(activity);

        noteEditor.setOnDismissListener((dialogInterface -> {
            NoteManager.addNewNote(noteEditor.getTitle(), noteEditor.getText());
            if (activity instanceof ThemeActivity)
                { ThemeNoteManager.addConnection(((ThemeActivity) activity).theme.id, NoteManager.getLastNoteId()); }

            updateAdapterSequence();
        }));

        noteEditor.show();
    }

    private void updateAdapterSequence() {
        int oldSize = activity.data.size();
        activity.reloadDataComparedToSearchBar();
        if (oldSize != activity.data.size())
            { activity.getAdapter().notifyItemInserted(activity.data.size() - 1); }
    }

    @SuppressLint("DiscouragedApi")
    public void changeColorByAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);
        button.setBackgroundResource(activity.getResources().getIdentifier(
                "add_" + appTheme,
                "drawable",
                activity.getPackageName()
        ));
    }
}