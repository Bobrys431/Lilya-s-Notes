package com.example.lilyasnotes.Buttons;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;

import com.example.lilyasnotes.Activities.AbstractActivity;
import com.example.lilyasnotes.Database.NoteManager;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.Database.ThemeManager;
import com.example.lilyasnotes.Widgets.Dialogs.ConfirmDialog;

public class DeleteButton {

    AbstractActivity activity;
    ImageButton button;

    public boolean isBlocked;

    public DeleteButton(ImageButton button) {
        this.button = button;
    }

    public void setup(AbstractActivity activity, View.OnClickListener onClickListener) {
        this.activity = activity;

        button.setOnClickListener((view) -> {
            if (!isBlocked) {
                onClickListener.onClick(button);

                new Handler().postDelayed(this::requestToDelete, 200);
            }
        });
    }

    private void requestToDelete() {
        String title = "???";
        if (activity.selectedViewType == AbstractActivity.THEME_TYPE)
            { title = ThemeManager.getTitle(activity.selectedViewId); }
        else if (activity.selectedViewType == AbstractActivity.NOTE_TYPE)
            { title = NoteManager.getTitle(activity.selectedViewId); }
        ConfirmDialog deletingConfirmationDialog = new ConfirmDialog();

        deletingConfirmationDialog.setTitle("Підтвердити видалення\n" + title);
        deletingConfirmationDialog.setOnConfirmListener(view -> {
            deletingConfirmationDialog.dismiss();

            if (activity.selectedViewType == AbstractActivity.THEME_TYPE)
                { ThemeManager.deleteTheme(activity.selectedViewId); }
            else if (activity.selectedViewType == AbstractActivity.NOTE_TYPE)
                { NoteManager.deleteNote(activity.selectedViewId); }

            updateAdapterSequence();
        });

        deletingConfirmationDialog.show(activity.getSupportFragmentManager(), "Deleting Confirmation Dialog");
    }

    private void updateAdapterSequence() {
        int index = activity.getSelectedViewIndex();
        activity.getAdapter().deselectSelectedViewHolder();

        activity.reloadDataComparedToSearchBar();
        activity.getAdapter().notifyItemRemoved(index);
    }

    @SuppressLint("DiscouragedApi")
    public void changeColorByAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);
        button.setBackgroundResource(activity.getResources().getIdentifier(
                "delete_" + appTheme,
                "drawable",
                activity.getPackageName()
        ));
    }
}
