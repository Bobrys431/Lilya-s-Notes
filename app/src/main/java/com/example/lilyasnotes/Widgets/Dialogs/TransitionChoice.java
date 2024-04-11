package com.example.lilyasnotes.Widgets.Dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;

public class TransitionChoice extends Dialog {

    final public static int TRANSITION_UP = 1;
    final public static int TRANSITION_DOWN = 2;

    private static int choiseType = TRANSITION_UP;

    public TransitionChoice(@NonNull Context context) {
        super(context);
    }

    @SuppressLint("DiscouragedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transition_choice);

        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(getContext());

        ImageButton rotationButton = findViewById(R.id.rotation_button);
        rotationButton.setBackgroundResource(getContext().getResources().getIdentifier(
                "switch_" + appTheme,
                "drawable",
                getContext().getPackageName()
        ));
        if (choiseType == TRANSITION_UP) rotationButton.setRotation(0);
        else rotationButton.setRotation(180);

        RelativeLayout basement = findViewById(R.id.basement);
        basement.setBackgroundColor(getContext().getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, getContext().getTheme()));

        rotationButton.setOnClickListener(view -> {
            if (choiseType == TRANSITION_UP) choiseType = TRANSITION_DOWN;
            else choiseType = TRANSITION_UP;
            if (choiseType == TRANSITION_UP) rotationButton.setRotation(0);
            else rotationButton.setRotation(180);
        });
    }

    public int getChoiseType() {
        return choiseType;
    }
}
