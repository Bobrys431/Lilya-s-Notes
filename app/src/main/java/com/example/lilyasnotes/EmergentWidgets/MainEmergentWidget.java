package com.example.lilyasnotes.EmergentWidgets;

import android.annotation.SuppressLint;
import android.content.Intent;

import androidx.activity.OnBackPressedCallback;

import com.example.lilyasnotes.Activities.ConsoleActivity;
import com.example.lilyasnotes.Activities.MainActivity;
import com.example.lilyasnotes.Buttons.ThemeButtons.MainThemeButton;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;

public class MainEmergentWidget extends EmergentWidget {

    private final MainActivity activity;

    public MainEmergentWidget(MainActivity activity) {
        super();
        this.activity = activity;
        themeButton = new MainThemeButton(activity);

        emergentWidgetFrame = activity.findViewById(R.id.emergent_widget);
        unfoldButtonFrame = activity.findViewById(R.id.unfold_button);
        unfoldIcon = activity.findViewById(R.id.unfold_icon);
        themeButtonView = activity.findViewById(R.id.theme_button);
        consoleButtonView = activity.findViewById(R.id.console_button);
    }

    @Override
    public void setup() {
        setupOnBackPressed();
        super.setup();
    }

    @Override
    protected float getTranslation() {
        return isActive ?
                activity.getActionBarLayout().getTranslationY() + activity.getActionBarLayout().getHeight() :
                activity.getActionBarLayout().getTranslationY() + activity.getActionBarLayout().getHeight() - emergentWidgetFrame.getHeight();
    }

    @Override
    protected void openConsole() {
        Intent intent = new Intent(activity, ConsoleActivity.class);
        intent.putExtra("type", ConsoleActivity.THEMES_TYPE);
        intent.putExtra("id", ConsoleActivity.THEMES_TYPE);
        activity.startActivity(intent);
    }

    private void setupOnBackPressed() {
        activity.getOnBackPressedDispatcher().addCallback(activity, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isActive) {
                    unfold();
                } else {
                    setEnabled(false);
                    activity.getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        });
    }

    @Override
    public void changeByAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);

        changeEmergentWidgetColor(appTheme);
        changeUnfoldButtonColor(appTheme);
        changeUnfoldIconColor(appTheme);
        changeThemeButtonColor(appTheme);
        changeConsoleButtonColor(appTheme);
    }

    @SuppressLint("DiscouragedApi")
    private void changeEmergentWidgetColor(String appTheme) {
        emergentWidgetFrame.setBackgroundResource(activity.getResources().getIdentifier(
                "emergent_widget_frame_" + appTheme,
                "drawable",
                activity.getPackageName()));
    }

    @SuppressLint("DiscouragedApi")
    private void changeUnfoldButtonColor(String appTheme) {
        unfoldButtonFrame.setBackgroundResource(activity.getResources().getIdentifier(
                "unfold_frame_" + appTheme,
                "drawable",
                activity.getPackageName()));
    }

    @SuppressLint("DiscouragedApi")
    private void changeUnfoldIconColor(String appTheme) {
        unfoldIcon.setBackgroundResource(activity.getResources().getIdentifier(
                "unfold_icon_" + appTheme,
                "drawable",
                activity.getPackageName()));
    }

    @SuppressLint("DiscouragedApi")
    private void changeThemeButtonColor(String appTheme) {
        themeButtonView.setBackgroundResource(activity.getResources().getIdentifier(
                "theme_" + appTheme,
                "drawable",
                activity.getPackageName()));
    }

    @SuppressLint("DiscouragedApi")
    private void changeConsoleButtonColor(String appTheme) {
        consoleButtonView.setBackgroundResource(activity.getResources().getIdentifier(
                "console_" + appTheme,
                "drawable",
                activity.getPackageName()));
    }
}
