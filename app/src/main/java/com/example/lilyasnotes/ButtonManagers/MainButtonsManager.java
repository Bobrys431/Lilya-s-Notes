package com.example.lilyasnotes.ButtonManagers;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lilyasnotes.Activities.MainActivity;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.Database.ThemeManager;
import com.example.lilyasnotes.Database.ThemesManager;
import com.example.lilyasnotes.Widgets.Buttons.AddButton;
import com.example.lilyasnotes.Widgets.Buttons.Button;
import com.example.lilyasnotes.Widgets.Buttons.DeleteButton;
import com.example.lilyasnotes.Widgets.Buttons.RenameButton;
import com.example.lilyasnotes.Widgets.Buttons.TransitionButton;
import com.example.lilyasnotes.Widgets.Dialogs.ConfirmDialog;
import com.example.lilyasnotes.Widgets.Dialogs.MainAddingChoise;
import com.example.lilyasnotes.Widgets.Dialogs.TextEnterer;
import com.example.lilyasnotes.Widgets.Dialogs.TransitionChoise;

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
        MainAddingChoise choise = new MainAddingChoise(activity);
        choise.setOnChoiseSelectedListener(() -> {
            int choiseType = choise.getChoiseType();

            if (choiseType == MainAddingChoise.THEME_CHOISE) {
                getTitleAndAddNewTheme();
            }
        });
        choise.show();
    }

    private void getTitleAndAddNewTheme() {
        TextEnterer enterer = new TextEnterer();
        enterer.setOnDismissListener(dialogInterface -> {
            String title = enterer.getText();
            ThemeManager.addNewTheme(title);
            ThemesManager.addNewTheme(ThemeManager.getLastThemeId());
            activity.reloadThemesFromDatabaseIntoThemesList();
            SQLiteDatabaseAdapter.printTable(SQLiteDatabaseAdapter.THEME, activity);
            SQLiteDatabaseAdapter.printTable(SQLiteDatabaseAdapter.THEMES, activity);
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
        String title = activity.getSelectedThemeTitle();
        ConfirmDialog deletingConfirmationDialog = new ConfirmDialog();

        deletingConfirmationDialog.setTitle("Підтвердити видалення\n" + title);
        deletingConfirmationDialog.setOnConfirmListener(view -> {
            deletingConfirmationDialog.dismiss();

            ThemeManager.deleteTheme(activity.selectedViewId);
            activity.reloadThemesFromDatabaseIntoThemesList();
            System.out.println("Succesfully deleted. " + activity.themes.size());
        });

        deletingConfirmationDialog.show(activity.getSupportFragmentManager(), "Deleting Confirmation Dialog");
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
        TransitionChoise transitionChoise = new TransitionChoise(activity);
        transitionChoise.setOnDismissListener(dialogInterface -> {
            if (
                    transitionChoise.getChoiseType() == TransitionChoise.TRANSITION_UP &&
                    ThemesManager.getThemeIndex(activity.selectedViewId) != 0) {
                System.out.println("UP");
                ThemesManager.translateThemeUp(activity.selectedViewId);
            } else if (
                    transitionChoise.getChoiseType() == TransitionChoise.TRANSITION_DOWN &&
                    ThemesManager.getThemeIndex(activity.selectedViewId) != activity.themes.size() - 1) {
                System.out.println("DOWN");
                ThemesManager.translateThemeDown(activity.selectedViewId);
            }
            activity.reloadThemesFromDatabaseIntoThemesList();
        });
        transitionChoise.show();
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
            ThemeManager.setThemeTitle(activity.selectedViewId, enterer.getText());
            activity.reloadThemesFromDatabaseIntoThemesList();
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
        if (activity.getSearchBar().isSearching) {
            hide(AddButton.class);
        } else {
            show(AddButton.class);
        }
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
            hide(RenameButton.class);
        } else {
            show(RenameButton.class);
        }
    }
}
