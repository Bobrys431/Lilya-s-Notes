package com.example.lilyasnotes.Widgets.Buttons;

import android.animation.ValueAnimator;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.lilyasnotes.Utilities.Tools;

public abstract class Button {

    private boolean isActive;
    private int position;

    protected ImageButton button;
    protected RelativeLayout frame;

    protected Button(ImageButton button, RelativeLayout frame) {
        this.button = button;
        this.frame = frame;
        isActive = true;
        position = 0;
        button.setOnClickListener(this::onClick);
    }

    public void show() {
        isActive = true;
    }

    public void hide() {
        isActive = false;
    }

    public void up() {
        position--;
    }

    public void down() {
        position++;
    }

    public void updateDisplay() {
        moveByY(
                frame.getTranslationY(),
                position * 56 * Tools.getDensity(button.getContext())
        );

        if (isActive) {
            if (frame.getTranslationX() == 0) return;
            moveByX(
                    50 * Tools.getDensity(button.getContext()),
                    0
            );
            button.setEnabled(true);
        } else {
            if (frame.getTranslationX() != 0) return;
            moveByX(
                    0,
                    50 * Tools.getDensity(button.getContext())
            );
            button.setEnabled(false);
        }
    }

    private void moveByY(float fromY, float toY) {
        ValueAnimator downTranslationForwardAnimator = ValueAnimator.ofFloat(fromY, toY);

        downTranslationForwardAnimator.setDuration(240);
        downTranslationForwardAnimator.addUpdateListener(valueAnimator ->
                frame.setTranslationY((float) valueAnimator.getAnimatedValue()));

        downTranslationForwardAnimator.start();
    }

    private void moveByX(float fromX, float toX) {
        ValueAnimator downTranslationForwardAnimator = ValueAnimator.ofFloat(fromX, toX);

        downTranslationForwardAnimator.setDuration(240);
        downTranslationForwardAnimator.addUpdateListener(valueAnimator ->
                frame.setTranslationX((float) valueAnimator.getAnimatedValue()));

        downTranslationForwardAnimator.start();
    }

    public boolean isActive() {
        return isActive;
    }

    public abstract void onClick(View view);

    public abstract void changeByAppTheme();
}
