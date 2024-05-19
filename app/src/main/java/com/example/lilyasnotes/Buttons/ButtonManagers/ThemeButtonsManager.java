package com.example.lilyasnotes.Buttons.ButtonManagers;

import android.content.DialogInterface;
import android.view.View;

import com.example.lilyasnotes.Activities.ThemeActivity;
import com.example.lilyasnotes.Database.NoteManager;
import com.example.lilyasnotes.Database.ThemeManager;
import com.example.lilyasnotes.Buttons.DTO.Button;
import com.example.lilyasnotes.Buttons.DTO.EditButton;
import com.example.lilyasnotes.Widgets.WidgetEditors.NoteWidgetEditor;
import com.example.lilyasnotes.Widgets.WidgetEditors.ThemeWidgetEditor;

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
            if (button instanceof EditButton) {
                updateRenameButton();
            }
        }
        for (Button button : buttons) {
            button.updateDisplay();
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
