package com.example.lilyasnotes.Buttons.ButtonManagers;

import android.content.DialogInterface;
import android.database.Cursor;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lilyasnotes.Activities.MainActivity;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.Database.ThemeManager;
import com.example.lilyasnotes.Database.ThemesManager;
import com.example.lilyasnotes.Buttons.DTO.AddButton;
import com.example.lilyasnotes.Buttons.DTO.Button;
import com.example.lilyasnotes.Buttons.DTO.DeleteButton;
import com.example.lilyasnotes.Buttons.DTO.EditButton;
import com.example.lilyasnotes.Buttons.DTO.TransitionButton;
import com.example.lilyasnotes.Widgets.Dialogs.ConfirmDialog;
import com.example.lilyasnotes.Widgets.Dialogs.MainAddingChoice;
import com.example.lilyasnotes.Widgets.Dialogs.TransitionChoice;
import com.example.lilyasnotes.Widgets.WidgetEditors.ThemeWidgetEditor;

import java.util.NoSuchElementException;

public class MainButtonsManager extends ButtonsManager {

    MainActivity activity;

    public MainButtonsManager(AppCompatActivity activity) {
        this.activity = (MainActivity) activity;
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
        MainAddingChoice choice = new MainAddingChoice(activity);
        choice.setOnChoiceSelectedListener(() -> {
            int choiceType = choice.getChoiceType();

            if (choiceType == MainAddingChoice.THEME_CHOICE) {
                getTitleAndAddNewTheme();
            }
        });
        choice.show();
    }

    private void getTitleAndAddNewTheme() {
        ThemeWidgetEditor themeEditor = new ThemeWidgetEditor(activity);

        DialogInterface.OnDismissListener onDismiss = dialogInterface -> {
            ThemeManager.addNewTheme(themeEditor.getTitle());
            ThemesManager.addConnection(ThemeManager.getLastThemeId());

            activity.getSearchBar().removeText();
            activity.reloadThemes();
        };

        themeEditor.setOnDismissListener(onDismiss);
        themeEditor.show();
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
        String title = getSelectedThemeTitle();
        ConfirmDialog deletingConfirmationDialog = new ConfirmDialog();

        deletingConfirmationDialog.setTitle("Підтвердити видалення\n" + title);
        deletingConfirmationDialog.setOnConfirmListener(view -> {
            deletingConfirmationDialog.dismiss();

            ThemeManager.deleteTheme(activity.selectedViewId);

            if (activity.getSearchBar().isSearching) {
                activity.getSearchBar().updateVisibleData();
                activity.getSearchBar().reloadData();
            } else
                activity.reloadThemes();
        });

        deletingConfirmationDialog.show(activity.getSupportFragmentManager(), "Deleting Confirmation Dialog");
    }

    private String getSelectedThemeTitle() {
        Cursor themeIdToTitle = SQLiteDatabaseAdapter.getDatabase(activity).rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_TITLE +
                " FROM " + SQLiteDatabaseAdapter.THEME +
                " WHERE " + SQLiteDatabaseAdapter.THEME_ID + " = " + activity.selectedViewId, null);

        String title;

        if (themeIdToTitle != null && themeIdToTitle.moveToFirst()) {
            title = themeIdToTitle.getString(themeIdToTitle.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_TITLE));
            themeIdToTitle.close();
        } else {
            throw new NoSuchElementException("There is no Theme with id " + activity.selectedViewId + " in " + SQLiteDatabaseAdapter.THEME);
        }

        return title;
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
            int index = ThemesManager.getThemeIndex(activity.selectedViewId);
            if (
                    transitionChoice.getChoiseType() == TransitionChoice.TRANSITION_UP &&
                    index != 0
            ) {
                ThemesManager.translateThemeUp(activity.selectedViewId);

            } else if (
                    transitionChoice.getChoiseType() == TransitionChoice.TRANSITION_DOWN &&
                    index != activity.themes.size() - 1
            ) {
                ThemesManager.translateThemeDown(activity.selectedViewId);
            }

            activity.reloadThemes();
        });
        transitionChoice.show();
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
        ThemeWidgetEditor themeEditor = new ThemeWidgetEditor(activity, activity.selectedViewId);

        DialogInterface.OnDismissListener onDismiss = dialogInterface -> {
            ThemeManager.setTitle(activity.selectedViewId, themeEditor.getTitle());

            if (activity.getSearchBar().isSearching)
                activity.getSearchBar().reloadData();
            else
                activity.reloadThemes();
        };

        themeEditor.setOnDismissListener(onDismiss);
        themeEditor.show();
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
        if (activity.selectedViewId == -1) {
            hide(DeleteButton.class);
        } else {
            show(DeleteButton.class);
        }
    }

    private void updateTransitionButton() {
        if (activity.selectedViewId == -1) {
            hide(TransitionButton.class);
        } else if (activity.getSearchBar().isSearching) {
            hide(TransitionButton.class);
        } else {
            show(TransitionButton.class);
        }
    }

    private void updateRenameButton() {
        if (activity.selectedViewId == -1) {
            hide(EditButton.class);
        } else {
            show(EditButton.class);
        }
    }
}
