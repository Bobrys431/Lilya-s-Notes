package com.example.lilyasnotes.Buttons;

import android.view.View;

import com.example.lilyasnotes.Activities.AbstractActivity;

public class EraseButton {

    AbstractActivity activity;
    View button;

    public EraseButton(View button) {
        this.button = button;
    }

    public void setup(AbstractActivity activity, View.OnClickListener onClickListener) {
        this.activity = activity;

        button.setOnClickListener((view) -> {
            switchEraseMode();
            onClickListener.onClick(button);
        });
    }

    private void switchEraseMode() {
        activity.eraseMode = !activity.eraseMode;
    }
}
