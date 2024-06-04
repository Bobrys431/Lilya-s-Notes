package com.example.lilyasnotes.EmergentWidget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.lilyasnotes.Activities.AbstractActivity;
import com.example.lilyasnotes.Activities.MainActivity;
import com.example.lilyasnotes.Activities.ThemeActivity;
import com.example.lilyasnotes.Buttons.ThemeButton;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.DatabaseManagement.ThemeManager;
import com.example.lilyasnotes.R;

public class EmergentWidget {

    private boolean isActive;
    private final AbstractActivity activity;
    private final ThemeButton themeButton;

    private final RelativeLayout emergentWidgetFrame;
    private final ImageButton themeButtonView;
    private EditText itemTitle;

    private final RelativeLayout unfoldButtonFrame;
    private final ImageView unfoldIcon;

    public EmergentWidget(AbstractActivity activity) {
        isActive = false;
        this.activity = activity;
        themeButton = new ThemeButton(activity);

        emergentWidgetFrame = activity.findViewById(R.id.emergent_widget);
        themeButtonView = activity.findViewById(R.id.theme_button);
        if (!(activity instanceof MainActivity)) itemTitle = activity.findViewById(R.id.item_title);

        unfoldButtonFrame = activity.findViewById(R.id.unfold_button);
        unfoldIcon = activity.findViewById(R.id.unfold_icon);
    }

    public void setup() {
        setupEmergentWidgetFrame();
        setupThemeButton();
        if (!(activity instanceof MainActivity)) setupItemTitle();

        setupUnfoldButtonFrame();
    }

    private void setupEmergentWidgetFrame() {
        new Handler().postDelayed(() -> {
            float difference = activity.getActionBarLayout().getHeight() - emergentWidgetFrame.getHeight();
            emergentWidgetFrame.setTranslationY(difference);
        }, 30);
    }

    private void setupThemeButton() {
        themeButtonView.setOnClickListener(view -> themeButton.changeAppTheme());
    }

    private void setupItemTitle() {
        if (activity instanceof ThemeActivity) {
            itemTitle.setText(((ThemeActivity) activity).theme.title);
        } else {
            return;
        }
        itemTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 45) {
                    Toast.makeText(activity, "Максимум символів: 45", Toast.LENGTH_LONG).show();
                    itemTitle.setText(charSequence.subSequence(0, charSequence.length() - 1));
                    itemTitle.setEnabled(false);
                    itemTitle.setEnabled(true);

                } else if (itemTitle.getLineCount() > 3) {
                    Toast.makeText(activity, "Максимум рядків: 3", Toast.LENGTH_LONG).show();
                    itemTitle.setText(charSequence.subSequence(0, charSequence.length() - 1));
                    itemTitle.setEnabled(false);
                    itemTitle.setEnabled(true);

                } else {
                    ThemeManager.setTitle(((ThemeActivity) activity).theme.id, charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setupUnfoldButtonFrame() {
        new Handler().postDelayed(() -> {
            float difference = activity.getActionBarLayout().getHeight() - emergentWidgetFrame.getHeight();
            unfoldButtonFrame.setTranslationY(difference);
        }, 30);

        unfoldButtonFrame.setOnClickListener(view -> {
            isActive = !isActive;

            updateEnabled();

            float translation = isActive ? activity.getActionBarLayout().getHeight() : activity.getActionBarLayout().getHeight() - emergentWidgetFrame.getHeight();
            ValueAnimator translationAnimator = ValueAnimator.ofFloat(emergentWidgetFrame.getTranslationY(), translation);
            translationAnimator.setDuration(400);
            translationAnimator.addUpdateListener(valueAnimator -> {
                emergentWidgetFrame.setTranslationY((Float) valueAnimator.getAnimatedValue());
                unfoldButtonFrame.setTranslationY((Float) valueAnimator.getAnimatedValue());
            });
            translationAnimator.start();

            float rotation = isActive ? 180f : 0f;
            ValueAnimator rotationAnimator = ValueAnimator.ofFloat(unfoldIcon.getRotation(), rotation);
            rotationAnimator.setDuration(150);
            rotationAnimator.addUpdateListener(valueAnimator -> unfoldIcon.setRotation((Float) valueAnimator.getAnimatedValue()));
            rotationAnimator.start();
        });
    }

    private void updateEnabled() {
        themeButtonView.setEnabled(isActive);
        if (!(activity instanceof MainActivity)) itemTitle.setEnabled(isActive);
    }

    public void changeByAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);

        changeEmergentWidgetFrameColor(appTheme);
        changeThemeButtonColor(appTheme);
        if (!(activity instanceof MainActivity)) changeItemTitleColor(appTheme);

        changeUnfoldButtonFrameColor(appTheme);
        changeUnfoldIconColor(appTheme);
    }

    @SuppressLint("DiscouragedApi")
    private void changeEmergentWidgetFrameColor(String appTheme) {
        emergentWidgetFrame.setBackgroundResource(activity.getResources().getIdentifier(
                "emergent_widget_frame_" + appTheme,
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

    private void changeItemTitleColor(String appTheme) {
        itemTitle.setTextColor(activity.getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeActiveColor :
                        R.color.darkThemeActiveColor));
    }

    @SuppressLint("DiscouragedApi")
    private void changeUnfoldButtonFrameColor(String appTheme) {
        System.out.println(appTheme);
        unfoldButtonFrame.setBackgroundResource(activity.getResources().getIdentifier(
                "unfold_frame_" + appTheme,
                "drawable",
                activity.getPackageName()));
    }

    @SuppressLint("DiscouragedApi")
    private void changeUnfoldIconColor(String appTheme) {
        unfoldIcon.setImageResource(activity.getResources().getIdentifier(
                "unfold_icon_" + appTheme,
                "drawable",
                activity.getPackageName()));
    }

    public ThemeButton getThemeButton() {
        return themeButton;
    }
}
