package com.example.lilyasnotes.Buttons;

import com.example.lilyasnotes.Activities.AbstractActivity;
import com.example.lilyasnotes.Activities.MainActivity;
import com.example.lilyasnotes.Activities.ThemeActivity;
import com.example.lilyasnotes.DatabaseManagement.NoteManager;
import com.example.lilyasnotes.DatabaseManagement.ThemeIntoManager;
import com.example.lilyasnotes.DatabaseManagement.ThemeManager;
import com.example.lilyasnotes.DatabaseManagement.ThemeNoteManager;
import com.example.lilyasnotes.DatabaseManagement.ThemesManager;
import com.example.lilyasnotes.AuxiliaryDialogs.MainAddingChoice;
import com.example.lilyasnotes.AuxiliaryDialogs.ThemeAddingChoice;
import com.example.lilyasnotes.Widgets.WidgetEditors.NoteWidgetEditor;
import com.example.lilyasnotes.Widgets.WidgetEditors.ThemeWidgetEditor;

public class AddButton {

    private AbstractActivity activity;

    public AddButton() {

    }

    public void setup(AbstractActivity activity) {
        this.activity = activity;
    }

    public void requestInsertion() {
        if (activity instanceof MainActivity) {
            requestInsertionInThemes();
        } else if (activity instanceof ThemeActivity) {
            requestInsertionInTheme();
        }
    }

    private void requestInsertionInThemes() {
        MainAddingChoice mac = new MainAddingChoice(activity);
        mac.setOnChoiceSelectedListener(() -> {
            mac.dismiss();

            int choiceType = mac.getChoiceType();
            if (choiceType == MainAddingChoice.THEME_CHOICE)
                { addNewTheme(); }
        });
        mac.show();
    }

    private void requestInsertionInTheme() {
        ThemeAddingChoice tac = new ThemeAddingChoice(activity);
        tac.setOnChoiceSelectedListener(() -> {
            tac.dismiss();

            int choiceType = tac.getChoiceType();
            if (choiceType == ThemeAddingChoice.THEME_CHOICE)
                { addNewTheme(); }
            else if (choiceType == ThemeAddingChoice.NOTE_CHOICE)
                { addNewNote(); }
        });
        tac.show();
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
}