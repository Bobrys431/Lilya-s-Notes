package com.example.lilyasnotes.Buttons.DTO;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;

public abstract class EditButton extends Button {

    public EditButton(AppCompatActivity activity) {
        super(
                activity.findViewById(R.id.edit_button),
                activity.findViewById(R.id.edit_button_frame));

        changeByAppTheme();
    }

    @SuppressLint("DiscouragedApi")
    @Override
    public void changeByAppTheme() {
        Context context = button.getContext();
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(context);

        button.setBackgroundResource(context.getResources().getIdentifier(
                "edit_" + appTheme,
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
