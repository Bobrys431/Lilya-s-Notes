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
import com.example.lilyasnotes.Widgets.TitleReceiver;

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
        TitleReceiver titleReceiver = new TitleReceiver(activity);

        titleReceiver.setOnDismissListener((dialogInterface) -> {
            ThemeManager.addNewTheme(titleReceiver.getTitle());

            if (activity instanceof MainActivity)
                { ThemesManager.addConnection(ThemeManager.getLastThemeId()); }
            else if (activity instanceof ThemeActivity)
                { ThemeIntoManager.addConnection(((ThemeActivity) activity).theme.id, ThemeManager.getLastThemeId()); }

            updateAdapterSequence();
        });

        titleReceiver.show();
    }

    private void addNewNote() {
        TitleReceiver titleReceiver = new TitleReceiver(activity);

        titleReceiver.setOnDismissListener((dialogInterface -> {
            NoteManager.addNewNote(titleReceiver.getTitle(), "");
            if (activity instanceof ThemeActivity)
                { ThemeNoteManager.addConnection(((ThemeActivity) activity).theme.id, NoteManager.getLastNoteId()); }

            updateAdapterSequence();
        }));

        titleReceiver.show();
    }

    private void updateAdapterSequence() {
        int oldSize = activity.data.size();
        activity.reloadDataComparedToSearchBar();
        if (oldSize != activity.data.size())
            { activity.getAdapter().notifyItemInserted(activity.data.size() - 1); }
    }
}