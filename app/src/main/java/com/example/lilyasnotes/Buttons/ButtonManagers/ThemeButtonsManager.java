package com.example.lilyasnotes.Buttons.ButtonManagers;

import android.content.DialogInterface;
import android.view.View;

import com.example.lilyasnotes.Activities.ThemeActivity;
import com.example.lilyasnotes.Database.NoteManager;
import com.example.lilyasnotes.Database.ThemeIntoManager;
import com.example.lilyasnotes.Database.ThemeManager;
import com.example.lilyasnotes.Buttons.DTO.Button;
import com.example.lilyasnotes.Buttons.DTO.EditButton;
import com.example.lilyasnotes.Database.ThemeNoteManager;
import com.example.lilyasnotes.Widgets.WidgetEditors.NoteWidgetEditor;
import com.example.lilyasnotes.Widgets.WidgetEditors.ThemeWidgetEditor;

import java.util.Random;

public class ThemeButtonsManager extends AbstractButtonsManager {

    public ThemeButtonsManager(ThemeActivity activity) {
        this.activity = activity;
    }

    @Override
    public void addAndSetupButtonsByType(Class<? extends Button>... buttonsTypes) {
        for (Class<? extends Button> buttonType : buttonsTypes) {
            if (isSuitable(buttonType)) {
                addAndSetupEditButton();
            }
        }
    }

    private boolean isSuitable(Class<? extends Button> buttonType) {
        return buttonType.isAssignableFrom(EditButton.class);
    }

    private void addAndSetupEditButton() {
        buttons.add(new EditButton(activity) {
            @Override
            public void onClick(View view) {
                editButtonRealization();
            }
        });
    }

    private void editButtonRealization() {
        System.out.println("ThemeButtonsManager editButtonRealization");

        int random = new Random().nextInt() % 2;

        if (random == 0) {
            ThemeWidgetEditor themeEditor = new ThemeWidgetEditor(activity, ThemeManager.getLastThemeId());

            DialogInterface.OnDismissListener onDismiss = dialogInterface -> {
                ThemeManager.setTitle(ThemeManager.getLastThemeId(), themeEditor.getTitle());

                int index = ThemeIntoManager.getThemeIndex(ThemeManager.getLastThemeId());
                int oldSize = activity.data.size();

                activity.reloadDataComparedToSearchBar();

                if (oldSize > activity.data.size()) {
                    activity.getAdapter().notifyItemRemoved(index);
                    activity.getButtonsManager().updateButtonsDisplay();
                } else
                    { activity.getAdapter().notifyItemChanged(index); }
            };

            themeEditor.setOnDismissListener(onDismiss);
            themeEditor.show();

        } else if (random == 1) {
            NoteWidgetEditor noteEditor = new NoteWidgetEditor(activity, NoteManager.getLastNoteId());

            DialogInterface.OnDismissListener onDismiss = dialogInterface -> {
                NoteManager.setTitle(NoteManager.getLastNoteId(), noteEditor.getTitle());
                NoteManager.setText(NoteManager.getLastNoteId(), noteEditor.getText());

                int index = ThemeNoteManager.getNoteIndex(NoteManager.getLastNoteId());
                int oldSize = activity.data.size();

                activity.reloadDataComparedToSearchBar();

                if (oldSize > activity.data.size()) {
                    activity.getAdapter().notifyItemRemoved(index);
                    activity.getButtonsManager().updateButtonsDisplay();
                } else
                    { activity.getAdapter().notifyItemChanged(index); }
            };

            noteEditor.setOnDismissListener(onDismiss);
            noteEditor.show();
        }
    }


    @Override
    public void updateButtonsDisplay() {
        for (Button button : buttons) {
            if (button instanceof EditButton) {
                updateRenameButton();
            }
        }
        for (Button button : buttons) {
            button.updateDisplay();
        }
    }

    private void updateRenameButton() {
        show(EditButton.class);
    }
}
