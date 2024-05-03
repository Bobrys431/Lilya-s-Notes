package com.example.lilyasnotes.Buttons.ButtonManagers;

import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;

import com.example.lilyasnotes.Activities.AbstractActivity;
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

public class ThemeButtonsManager extends AbstractButtonsManager {

    public ThemeButtonsManager(ThemeActivity activity) {
        this.activity = activity;
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
        System.out.println("ThemeButtonsManager addButtonRealization");

        ThemeAddingChoice choice = new ThemeAddingChoice(activity);
        choice.setOnChoiceSelectedListener(() -> {
            int choiceType = choice.getChoiceType();

            if (choiceType == AbstractActivity.THEME_TYPE) {
                getTitleAndAddNewTheme();
            } else if (choiceType == AbstractActivity.NOTE_TYPE) {
                getTitleAndAddNewNote();
            }
        });
        choice.show();
    }

    private void getTitleAndAddNewTheme() {
        ThemeWidgetEditor themeEditor = new ThemeWidgetEditor(activity);

        DialogInterface.OnDismissListener onDismiss = dialogInterface -> {
            ThemeManager.addNewTheme(themeEditor.getTitle());
            ThemeIntoManager.addConnection(((ThemeActivity) activity).theme.id, ThemeManager.getLastThemeId());

            int oldSize = activity.data.size();
            activity.reloadDataComparedToSearchBar();

            if (oldSize != activity.data.size()) {
                activity.getAdapter().notifyItemInserted(activity.data.size() - 1);
                new Handler().postDelayed(() -> {
                    activity.getAdapter().selectViewHolder(activity.data.size() - 1);
                    updateButtonsDisplay();
                }, 1);
            }
        };

        themeEditor.setOnDismissListener(onDismiss);
        themeEditor.show();
    }

    private void getTitleAndAddNewNote() {
        NoteWidgetEditor noteEditor = new NoteWidgetEditor(activity);

        DialogInterface.OnDismissListener onDismiss = dialogInterface -> {
            NoteManager.addNewNote(noteEditor.getTitle(), noteEditor.getText());
            ThemeNoteManager.addConnection(((ThemeActivity) activity).theme.id, NoteManager.getLastNoteId());

            int oldSize = activity.data.size();
            activity.reloadDataComparedToSearchBar();

            if (oldSize != activity.data.size()) {
                activity.getAdapter().notifyItemInserted(activity.data.size() - 1);
                new Handler().postDelayed(() -> {
                    activity.getAdapter().selectViewHolder(activity.data.size() - 1);
                    updateButtonsDisplay();
                }, 1);
            }
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
        System.out.println("ThemeButtonsManager deleteButtonRealization");

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

            int index = activity.getSelectedViewIndex();
            activity.getAdapter().deselectSelectedViewHolder();

            activity.reloadDataComparedToSearchBar();
            activity.getAdapter().notifyItemRemoved(index);
            updateButtonsDisplay();
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
        System.out.println("ThemeButtonsManager editButtonRealization");

        if (activity.selectedViewType == ThemeActivity.THEME_TYPE) {
            ThemeWidgetEditor themeEditor = new ThemeWidgetEditor(activity, activity.selectedViewId);

            DialogInterface.OnDismissListener onDismiss = dialogInterface -> {
                ThemeManager.setTitle(activity.selectedViewId, themeEditor.getTitle());

                int index = activity.getSelectedViewIndex();
                int oldSize = activity.data.size();

                activity.reloadDataComparedToSearchBar();

                if (oldSize > activity.data.size()) {
                    activity.getAdapter().notifyItemRemoved(index);
                    activity.getAdapter().deselectSelectedViewHolder();
                    activity.getButtonsManager().updateButtonsDisplay();
                } else
                    { activity.getAdapter().notifyItemChanged(index); }
            };

            themeEditor.setOnDismissListener(onDismiss);
            themeEditor.show();

        } else if (activity.selectedViewType == ThemeActivity.NOTE_TYPE) {
            NoteWidgetEditor noteEditor = new NoteWidgetEditor(activity, activity.selectedViewId);

            DialogInterface.OnDismissListener onDismiss = dialogInterface -> {
                NoteManager.setTitle(activity.selectedViewId, noteEditor.getTitle());
                NoteManager.setText(activity.selectedViewId, noteEditor.getText());

                int index = activity.getSelectedViewIndex();
                int oldSize = activity.data.size();

                activity.reloadDataComparedToSearchBar();

                if (oldSize > activity.data.size()) {
                    activity.getAdapter().notifyItemRemoved(index);
                    activity.getAdapter().deselectSelectedViewHolder();
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
