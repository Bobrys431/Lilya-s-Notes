package com.example.lilyasnotes.Buttons.ButtonManagers;

import android.content.DialogInterface;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lilyasnotes.Activities.ThemeActivity;
import com.example.lilyasnotes.Database.NoteManager;
import com.example.lilyasnotes.Database.ThemeIntoManager;
import com.example.lilyasnotes.Database.ThemeManager;
import com.example.lilyasnotes.Database.ThemeNoteManager;
import com.example.lilyasnotes.Buttons.DTO.AddButton;
import com.example.lilyasnotes.Buttons.DTO.Button;
import com.example.lilyasnotes.Buttons.DTO.DeleteButton;
import com.example.lilyasnotes.Buttons.DTO.EditButton;
import com.example.lilyasnotes.Widgets.Dialogs.ConfirmDialog;
import com.example.lilyasnotes.Widgets.Dialogs.ThemeAddingChoice;
import com.example.lilyasnotes.Widgets.WidgetEditors.NoteWidgetEditor;
import com.example.lilyasnotes.Widgets.WidgetEditors.ThemeWidgetEditor;

public class ThemeButtonsManager extends ButtonsManager {

    ThemeActivity activity;

    public ThemeButtonsManager(AppCompatActivity activity) {
        this.activity = (ThemeActivity) activity;
    }

    @Override
    public void addAndSetupButtonsByType(Class<? extends Button>... buttonsTypes) {
        for (Class<? extends Button> buttonType : buttonsTypes) {
            if (isSuitable(buttonType, AddButton.class)) {
                addAndSetupAddButton();
            }

            if (isSuitable(buttonType, DeleteButton.class)) {
                addAndSetupDeleteButton();
            }

            if (isSuitable(buttonType, EditButton.class)) {
                addAndSetupEditButton();
            }
        }
    }

    private boolean isSuitable(Class<? extends Button> buttonType, Class<? extends Button> comparingType) {
        return buttonType.isAssignableFrom(comparingType);
    }

    private void addAndSetupAddButton() {
        buttons.add(new AddButton(activity) {
            @Override
            public void onClick(View view) {
                addButtonRealization();
            }
        });
    }

    private void addButtonRealization() {
        ThemeAddingChoice choice = new ThemeAddingChoice(activity);
        choice.setOnChoiceSelectedListener(() -> {
            int choiceType = choice.getChoiceType();

            if (choiceType == ThemeAddingChoice.THEME_CHOICE) {
                getTitleAndAddNewTheme();
            } else if (choiceType == ThemeAddingChoice.NOTE_CHOICE) {
                getTitleAndAddNewNote();
            }
        });
        choice.show();
    }

    private void getTitleAndAddNewTheme() {
        ThemeWidgetEditor themeEditor = new ThemeWidgetEditor(activity);

        DialogInterface.OnDismissListener onDismiss = dialogInterface -> {
            ThemeManager.addNewTheme(themeEditor.getTitle());
            ThemeIntoManager.addConnection(activity.theme.id, ThemeManager.getLastThemeId());

            activity.getSearchBar().removeText();
            activity.reloadData();
        };

        themeEditor.setOnDismissListener(onDismiss);
        themeEditor.show();
    }

    private void getTitleAndAddNewNote() {
        NoteWidgetEditor noteEditor = new NoteWidgetEditor(activity);

        DialogInterface.OnDismissListener onDismiss = dialogInterface -> {
            NoteManager.addNewNote(noteEditor.getTitle(), noteEditor.getText());
            ThemeNoteManager.addConnection(activity.theme.id, NoteManager.getLastNoteId());

            activity.getSearchBar().removeText();
            activity.reloadData();
        };

        noteEditor.setOnDismissListener(onDismiss);
        noteEditor.show();
    }

    private void addAndSetupDeleteButton() {
        buttons.add(new DeleteButton(activity) {
            @Override
            public void onClick(View view) {
                deleteButtonRealization();
            }
        });
    }

    private void deleteButtonRealization() {
        String title = getSelectedDataTitle();
        ConfirmDialog deletingConfirmationDialog = new ConfirmDialog();

        deletingConfirmationDialog.setTitle("Підтвердити видалення\n" + title);
        deletingConfirmationDialog.setOnConfirmListener(view -> {
            deletingConfirmationDialog.dismiss();

            if (activity.selectedViewType == ThemeActivity.THEME_TYPE) {
                ThemeManager.deleteTheme(activity.selectedViewId);
            } else if (activity.selectedViewType == ThemeActivity.NOTE_TYPE) {
                NoteManager.deleteNote(activity.selectedViewId);
            }

            if (activity.getSearchBar().isSearching) {
                activity.getSearchBar().updateVisibleData();
                activity.getSearchBar().reloadData();
            } else
                activity.reloadData();
        });

        deletingConfirmationDialog.show(activity.getSupportFragmentManager(), "Deleting Confirmation Dialog");
    }

    private String getSelectedDataTitle() {
        return activity.selectedViewType == ThemeActivity.THEME_TYPE ?
                ThemeManager.getTitle(activity.selectedViewId) :
                NoteManager.getTitle(activity.selectedViewId);
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
        if (activity.selectedViewType == ThemeActivity.THEME_TYPE) {
            ThemeWidgetEditor themeEditor = new ThemeWidgetEditor(activity, activity.selectedViewId);

            DialogInterface.OnDismissListener onDismiss = dialogInterface -> {
                ThemeManager.setTitle(activity.selectedViewId, themeEditor.getTitle());

                if (activity.getSearchBar().isSearching)
                    activity.getSearchBar().reloadData();
                else
                    activity.reloadData();
            };

            themeEditor.setOnDismissListener(onDismiss);
            themeEditor.show();

        } else if (activity.selectedViewType == ThemeActivity.NOTE_TYPE) {
            NoteWidgetEditor noteEditor = new NoteWidgetEditor(activity, activity.selectedViewId);

            DialogInterface.OnDismissListener onDismiss = dialogInterface -> {
                NoteManager.setTitle(activity.selectedViewId, noteEditor.getTitle());
                NoteManager.setText(activity.selectedViewId, noteEditor.getText());

                if (activity.getSearchBar().isSearching)
                    activity.getSearchBar().reloadData();
                else
                    activity.reloadData();
            };

            noteEditor.setOnDismissListener(onDismiss);
            noteEditor.show();
        }
    }


    @Override
    public void updateButtonsDisplay() {
        for (Button button : buttons) {
            if (button instanceof AddButton) {
                updateAddButton();
            }
            if (button instanceof DeleteButton) {
                updateDeleteButton();
            }
            if (button instanceof EditButton) {
                updateRenameButton();
            }
        }
        for (Button button : buttons) {
            button.updateDisplay();
        }
    }

    private void updateAddButton() {
        show(AddButton.class);
    }

    private void updateDeleteButton() {
        if (activity.selectedViewType == ThemeActivity.NO_TYPE) {
            hide(DeleteButton.class);
        } else {
            show(DeleteButton.class);
        }
    }

    private void updateRenameButton() {
        if (activity.selectedViewType == ThemeActivity.NO_TYPE) {
            hide(EditButton.class);
        } else {
            show(EditButton.class);
        }
    }
}
