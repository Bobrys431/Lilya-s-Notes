package com.example.lilyasnotes.EmergentWidgets;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.res.ResourcesCompat;

import com.example.lilyasnotes.Activities.ConsoleActivity;
import com.example.lilyasnotes.Activities.NoteActivity;
import com.example.lilyasnotes.Buttons.ThemeButtons.NoteThemeButton;
import com.example.lilyasnotes.DatabaseManagement.NoteManager;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;

public class NoteEmergentWidget extends EmergentWidget {

    private final NoteActivity activity;

    private final EditText title;

    public NoteEmergentWidget(NoteActivity activity) {
        super();
        this.activity = activity;
        themeButton = new NoteThemeButton(activity);

        emergentWidgetFrame = activity.findViewById(R.id.emergent_widget);
        unfoldButtonFrame = activity.findViewById(R.id.unfold_button);
        unfoldIcon = activity.findViewById(R.id.unfold_icon);
        themeButtonView = activity.findViewById(R.id.theme_button);
        title = activity.findViewById(R.id.title);
        consoleButtonView = activity.findViewById(R.id.console_button);
    }

    @Override
    public void setup() {
        setupTitle();
        setupOnBackPressed();
        super.setup();
    }

    private void setupTitle() {
        title.setText(NoteManager.getTitle(activity.note.id));
        title.setTypeface(ResourcesCompat.getFont(activity, R.font.advent_pro_bold));

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (title.getLineCount() > 3) {
                    Toast.makeText(activity, "Максимум рядків: 3", Toast.LENGTH_LONG).show();
                    title.setText(charSequence.subSequence(0, charSequence.length() - 1));
                    title.setEnabled(false);
                    title.setEnabled(true);
                } else {
                    NoteManager.setTitle(activity.note.id, charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        title.setOnFocusChangeListener((view, b) -> {
            View decorView = activity.getWindow().getDecorView();
            if (b) {
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            } else {
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        });
    }

    @Override
    protected void enable() {
        super.enable();
        title.setEnabled(isActive);
    }

    @Override
    protected float getTranslation() {
        return isActive ?
                0f :
                -emergentWidgetFrame.getHeight();
    }

    @Override
    protected void openConsole() {
        Intent intent = new Intent(activity, ConsoleActivity.class);
        intent.putExtra("type", ConsoleActivity.NOTE_TYPE);
        intent.putExtra("id", activity.note.id);
        activity.startActivity(intent);
    }

    private void setupOnBackPressed() {
        activity.getOnBackPressedDispatcher().addCallback(activity, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (title.isFocused()) {
                    title.setEnabled(false);
                    title.setEnabled(true);
                } else if (isActive) {
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
        changeDataTitleColor(appTheme);
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

    private void changeDataTitleColor(String appTheme) {
        title.setTextColor(activity.getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeActiveColor :
                        R.color.darkThemeActiveColor));
    }
}
