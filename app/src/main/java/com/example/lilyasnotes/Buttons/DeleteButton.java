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

import java.util.Random;

public class DeleteButton {

    AbstractActivity activity;
    ImageButton button;

    public DeleteButton(ImageButton button) {
        this.button = button;
    }

    public void setup(AbstractActivity activity, View.OnClickListener onClickListener) {
        this.activity = activity;

        button.setOnClickListener((view) -> {
            onClickListener.onClick(button);
            new Handler().postDelayed(this::requestToDelete, 200);
        });
    }

    private void requestToDelete() {
        String title = "???";
        int random = new Random().nextInt() % 2;
        if (random == 0)
            { title = ThemeManager.getTitle(ThemeManager.getLastThemeId()); }
        else if (random == 1)
            { title = NoteManager.getTitle(NoteManager.getLastNoteId()); }
        ConfirmDialog deletingConfirmationDialog = new ConfirmDialog();

        deletingConfirmationDialog.setTitle("Підтвердити видалення\n" + title);
        deletingConfirmationDialog.setOnConfirmListener(view -> {
            deletingConfirmationDialog.dismiss();

            if (random == 0)
                { ThemeManager.deleteTheme(ThemeManager.getLastThemeId()); }
            else if (random == 1)
                { NoteManager.deleteNote(NoteManager.getLastNoteId()); }

            updateAdapterSequence();
        });

        deletingConfirmationDialog.show(activity.getSupportFragmentManager(), "Deleting Confirmation Dialog");
    }

    private void updateAdapterSequence() {
        int index = activity.getAdapter().getItemCount() - 1;

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
