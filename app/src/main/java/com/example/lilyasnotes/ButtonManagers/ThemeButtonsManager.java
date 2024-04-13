package com.example.lilyasnotes.ButtonManagers;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lilyasnotes.Activities.ThemeActivity;
import com.example.lilyasnotes.Database.NoteManager;
import com.example.lilyasnotes.Database.ThemeIntoManager;
import com.example.lilyasnotes.Database.ThemeManager;
import com.example.lilyasnotes.Database.ThemeNoteManager;
import com.example.lilyasnotes.Widgets.Buttons.AddButton;
import com.example.lilyasnotes.Widgets.Buttons.Button;
import com.example.lilyasnotes.Widgets.Buttons.DeleteButton;
import com.example.lilyasnotes.Widgets.Buttons.RenameButton;
import com.example.lilyasnotes.Widgets.Buttons.TransitionButton;
import com.example.lilyasnotes.Widgets.Dialogs.ConfirmDialog;
import com.example.lilyasnotes.Widgets.Dialogs.TextEnterer;
import com.example.lilyasnotes.Widgets.Dialogs.ThemeAddingChoice;
import com.example.lilyasnotes.Widgets.Dialogs.TransitionChoice;

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

            if (isSuitable(buttonType, TransitionButton.class)) {
                addAndSetupTransitionButton();
            }

            if (isSuitable(buttonType, RenameButton.class)) {
                addAndSetupRenameButton();
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
    }

    private void getTitleAndAddNewTheme() {
        TextEnterer enterer = new TextEnterer();
        enterer.setOnDismissListener(dialogInterface -> {
            String title = enterer.getText();

            ThemeManager.addNewTheme(title);
            ThemeIntoManager.addConnection(activity.theme.id, ThemeManager.getLastThemeId());

            activity.getSearchBar().removeText();
            activity.reloadData();
        });
        enterer.show(activity.getSupportFragmentManager(), "Get text");
    }

    private void getTitleAndAddNewNote() {
        TextEnterer enterer = new TextEnterer();
        enterer.setOnDismissListener(dialogInterface -> {
            String title = enterer.getText();

            NoteManager.addNewNote(title);
            ThemeNoteManager.addConnection(activity.theme.id, NoteManager.getLastNoteId());

            activity.getSearchBar().removeText();
            activity.reloadData();
        });
        enterer.show(activity.getSupportFragmentManager(), "Get text");
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

    private void addAndSetupTransitionButton() {
        buttons.add(new TransitionButton(activity) {
            @Override
            public void onClick(View view) {
                transitionButtonRealization();
            }
        });
    }

    private void transitionButtonRealization() {
        TransitionChoice transitionChoice = new TransitionChoice(activity);
        transitionChoice.setOnDismissListener(dialogInterface -> {

            int index =
                    activity.selectedViewType == ThemeActivity.THEME_TYPE ?
                            ThemeIntoManager.getThemeIndex(activity.selectedViewId) :
                            ThemeNoteManager.getNoteIndex(activity.selectedViewId);

            if (
                    transitionChoice.getChoiseType() == TransitionChoice.TRANSITION_UP &&
                    index != 0
            ) {
                if (activity.selectedViewType == ThemeActivity.THEME_TYPE) {
                    ThemeIntoManager.translateThemeUp(activity.theme.id, activity.selectedViewId);
                } else {
                    ThemeNoteManager.translateNoteUp(activity.theme.id, activity.selectedViewId);
                }

            } else if (
                    transitionChoice.getChoiseType() == TransitionChoice.TRANSITION_DOWN &&
                    index != activity.data.size() - 1
            ) {
                if (activity.selectedViewType == ThemeActivity.THEME_TYPE) {
                    ThemeIntoManager.translateThemeDown(activity.theme.id, activity.selectedViewId);
                } else {
                    ThemeNoteManager.translateNoteDown(activity.theme.id, activity.selectedViewId);
                }
            }

            activity.reloadData();
        });
        transitionChoice.show();
    }

    private void addAndSetupRenameButton() {
        buttons.add(new RenameButton(activity) {
            @Override
            public void onClick(View view) {
                renameButtonRealization();
            }
        });
    }

    private void renameButtonRealization() {
        TextEnterer enterer = new TextEnterer();
        enterer.setOnDismissListener(dialogInterface -> {
            if (activity.selectedViewType == ThemeActivity.THEME_TYPE) {
                ThemeManager.setTitle(activity.selectedViewId, enterer.getText());
            } if (activity.selectedViewId == ThemeActivity.NOTE_TYPE) {
                NoteManager.setTitle(activity.selectedViewId, enterer.getText());
            }

            if (activity.getSearchBar().isSearching)
                activity.getSearchBar().reloadData();
            else
                activity.reloadData();
        });
        enterer.show(activity.getSupportFragmentManager(), "Get text");
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
            if (button instanceof TransitionButton) {
                updateTransitionButton();
            }
            if (button instanceof RenameButton) {
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

    private void updateTransitionButton() {
        if (activity.selectedViewType == ThemeActivity.NO_TYPE) {
            hide(TransitionButton.class);
        } else if (activity.getSearchBar().isSearching) {
            hide(TransitionButton.class);
        } else {
            show(TransitionButton.class);
        }
    }

    private void updateRenameButton() {
        if (activity.selectedViewType == ThemeActivity.NO_TYPE) {
            hide(RenameButton.class);
        } else {
            show(RenameButton.class);
        }
    }
}
