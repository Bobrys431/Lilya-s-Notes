package com.example.lilyasnotes.Buttons;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;

import com.example.lilyasnotes.Activities.AbstractActivity;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;

public class EraseButton {

    AbstractActivity activity;
    ImageButton button;

    public EraseButton(ImageButton button) {
        this.button = button;
    }

    public void setup(AbstractActivity activity, View.OnClickListener onClickListener) {
        this.activity = activity;

        button.setOnClickListener((view) -> {
            onClickListener.onClick(button);
            new Handler().postDelayed(this::switchEraseMode, 200);
        });
    }

    private void switchEraseMode() {
        activity.eraseMode = !activity.eraseMode;
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
