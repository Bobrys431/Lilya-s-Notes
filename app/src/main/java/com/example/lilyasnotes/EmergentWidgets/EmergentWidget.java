package com.example.lilyasnotes.EmergentWidgets;

import android.animation.ValueAnimator;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.lilyasnotes.Buttons.ThemeButtons.ThemeButton;

public abstract class EmergentWidget {

    protected boolean isActive;
    protected ThemeButton themeButton;

    protected RelativeLayout emergentWidgetFrame;
    protected RelativeLayout unfoldButtonFrame;
    protected ImageView unfoldIcon;
    protected ImageButton themeButtonView;

    public EmergentWidget() {
        isActive = false;
    }

    public void setup() {
        unfoldButtonFrame.setOnClickListener(view -> unfold());
        themeButtonView.setOnClickListener(view -> themeButton.changeAppTheme());

        new Handler().postDelayed(() -> {
            emergentWidgetFrame.setTranslationY(getTranslation());
            unfoldButtonFrame.setTranslationY(getTranslation());
            unfoldIcon.setRotation(getRotation());
        }, 50);

    }

    private void unfold() {
        isActive = !isActive;
        enable();
        animate();
    }

    protected void enable() {
        emergentWidgetFrame.setEnabled(isActive);
        themeButtonView.setEnabled(isActive);
    }

    private void animate() {
        ValueAnimator translationAnimator = ValueAnimator.ofFloat(emergentWidgetFrame.getTranslationY(), getTranslation());
        translationAnimator.setDuration(400);
        translationAnimator.addUpdateListener(valueAnimator -> {
            emergentWidgetFrame.setTranslationY((Float) valueAnimator.getAnimatedValue());
            unfoldButtonFrame.setTranslationY((Float) valueAnimator.getAnimatedValue());
        });
        translationAnimator.start();

        ValueAnimator rotationAnimator = ValueAnimator.ofFloat(unfoldIcon.getRotation(), getRotation());
        rotationAnimator.setDuration(150);
        rotationAnimator.addUpdateListener(valueAnimator -> unfoldIcon.setRotation((Float) valueAnimator.getAnimatedValue()));
        rotationAnimator.start();
    }

    private float getRotation() {
        if (isActive) return 180f;
        else return 0f;
    }

    protected abstract float getTranslation();

    public abstract void changeByAppTheme();

    public ThemeButton getThemeButton() {
        return themeButton;
    }
}
