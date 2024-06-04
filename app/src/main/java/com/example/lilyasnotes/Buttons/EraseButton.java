package com.example.lilyasnotes.Buttons;

import com.example.lilyasnotes.Activities.AbstractActivity;

public class EraseButton {

    private AbstractActivity activity;

    public EraseButton() {

    }

    public void setup(AbstractActivity activity) {
        this.activity = activity;
    }

    public void switchEraseMode() {
        activity.eraseMode = !activity.eraseMode;
    }
}
