package com.example.lilyasnotes.Widgets.Buttons;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;

public abstract class AddButton extends Button {

    public AddButton(AppCompatActivity activity) {
        super(
                activity.findViewById(R.id.add_button),
                activity.findViewById(R.id.add_button_frame));

        changeByAppTheme();
    }

    @SuppressLint("DiscouragedApi")
    @Override
    public void changeByAppTheme() {
        Context context = button.getContext();
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(context);

        button.setBackgroundResource(context.getResources().getIdentifier(
                "add_" + appTheme,
                "drawable",
                context.getPackageName()
        ));
        frame.setBackgroundResource(context.getResources().getIdentifier(
                "click_button_bg_" + appTheme,
                "drawable",
                context.getPackageName()
        ));
    }
}
