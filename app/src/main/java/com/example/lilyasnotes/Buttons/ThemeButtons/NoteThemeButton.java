package com.example.lilyasnotes.Buttons.ThemeButtons;

import android.view.Window;

import com.example.lilyasnotes.Activities.NoteActivity;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;

public class NoteThemeButton extends ThemeButton {

    private final NoteActivity activity;

    public NoteThemeButton(NoteActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void changeAllViewsByAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);

        changeWindowSettingsByAppTheme(appTheme);
        changeBasementByAppTheme(appTheme);
        changeTextByAppTheme(appTheme);
        changeEmergentWidgetByAppTheme();
    }

    private void changeWindowSettingsByAppTheme(String appTheme) {
        Window window = activity.getWindow();

        window.setStatusBarColor(activity.getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground));

        window.getDecorView().setBackgroundColor(activity.getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeAdditionalColor :
                        R.color.darkThemeAdditionalColor));

        window.setNavigationBarColor(activity.getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground));
    }

    private void changeBasementByAppTheme(String appTheme) {
        activity.getBasement().setBackgroundColor(activity.getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeAdditionalColor :
                        R.color.darkThemeAdditionalColor));
    }

    private void changeTextByAppTheme(String appTheme) {
        activity.getEditText().setTextColor(activity.getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeActiveColor :
                        R.color.darkThemeActiveColor));

        activity.getEditText().setHintTextColor(activity.getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeHintColor :
                        R.color.darkThemeHintColor));
    }

    private void changeEmergentWidgetByAppTheme() {
        activity.getEmergentWidget().changeByAppTheme();
    }
}
