package com.example.lilyasnotes.Data.ViewHolders;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Activities.AbstractActivity;
import com.example.lilyasnotes.Activities.ThemeActivity;
import com.example.lilyasnotes.Database.ThemeManager;
import com.example.lilyasnotes.R;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.Utilities.Tools;

public class ThemeViewHolder extends AbstractViewHolder {

    private int id;

    public RelativeLayout basement;
    public ImageView mark;
    public TextView title;
    public RelativeLayout titleFrame;

    public ThemeViewHolder(@NonNull View itemView) {
        super(itemView);

        basement = itemView.findViewById(R.id.basement);
        mark = itemView.findViewById(R.id.mark);
        title = itemView.findViewById(R.id.title);
        titleFrame = itemView.findViewById(R.id.title_frame);
    }

    @Override
    public void setup(int id, AbstractActivity activity) {
        this.id = id;
        this.activity = activity;
        isSelected = // TODO
                activity.selectedViewId == id &&
                activity.selectedViewType == AbstractActivity.THEME_TYPE;

        if (isSelected) { // TODO
            alphaState = ALPHA_SELECTED;
            marginState = (int) (Tools.getDensity(activity) * MARGIN_SELECTED);

        } else if (activity.selectedViewType == AbstractActivity.NO_TYPE) { // TODO
            alphaState = ALPHA_TO_SELECT;
            marginState = (int) (Tools.getDensity(activity) * MARGIN_NOT_SELECTED);

        } else { // TODO
            alphaState = ALPHA_NOT_SELECTED;
            marginState = (int) (Tools.getDensity(activity) * MARGIN_NOT_SELECTED);
        }

        setupBasement();
        setupTitle();
        setupMark();

        changeColorByAppTheme();
        visualizeDelayed(); // TODO
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupBasement() {
        GestureDetector gestureDetector = new GestureDetector(activity, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(@NonNull MotionEvent e) { // TODO
                Intent intent = new Intent(activity, ThemeActivity.class);
                intent.putExtra("themeId", id);
                activity.startActivity(intent);
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
                if (isSelected) // TODO
                    { activity.getAdapter().deselectSelectedViewHolder(); }
                else
                    { activity.getAdapter().selectViewHolder(getAdapterPosition()); }
                activity.getButtonsManager().updateButtonsDisplay();
                return true;
            }
        });

        basement.setOnTouchListener((view, motionEvent) -> {
            gestureDetector.onTouchEvent(motionEvent);
            return true;
        });
    }

    private void setupTitle() {
        title.setText(ThemeManager.getTitle(id));
        title.setTypeface(ResourcesCompat.getFont(activity, R.font.advent_pro_bold));
    }

    private void visualizeDelayed() { // TODO
        new Handler().postDelayed(() -> {

            basement.setAlpha(alphaState);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) basement.getLayoutParams();
            params.setMargins(0, marginState, 0, marginState);
            basement.setLayoutParams(params);

            int width;
            int height;
            if (isSelected) {
                width = (int) (Tools.getDensity(activity) * 290);
                height = (int) (titleFrame.getHeight() * 1.1f);
            } else {
                width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            }

            RelativeLayout.LayoutParams frameParams = new RelativeLayout.LayoutParams(
                    width,
                    height);
            titleFrame.setLayoutParams(frameParams);
        }, 300);
    }

    private void setupMark() {
        ViewTreeObserver vto = titleFrame.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                titleFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ViewGroup.LayoutParams markLayoutParams = mark.getLayoutParams();
                markLayoutParams.height = titleFrame.getHeight();
                markLayoutParams.width = titleFrame.getHeight() / 2;
                mark.setLayoutParams(markLayoutParams);
            }
        });
    }

    @Override
    public void updateSelection() { // TODO
        if (activity.selectedViewType == AbstractActivity.THEME_TYPE && activity.selectedViewId == id && !isSelected) {
            isSelected = true;

            setupWrapParams();
            animateSelected();

        } else if (isSelected) {
            isSelected = false;

            setupWrapParams();
            animateDeselected();
        }
    }

    private void animateSelected() { // TODO
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(
                PropertyValuesHolder.ofInt("margins", (int) (MARGIN_NOT_SELECTED * Tools.getDensity(activity)), (int) (MARGIN_SELECTED * Tools.getDensity(activity))),
                PropertyValuesHolder.ofInt("width", titleFrame.getWidth(), (int) (290 * Tools.getDensity(activity))),
                PropertyValuesHolder.ofInt("height", titleFrame.getHeight(), (int) (titleFrame.getHeight() * 1.2f))
        );
        animator.setDuration(300);

        RecyclerView.LayoutParams basementParams = (RecyclerView.LayoutParams) basement.getLayoutParams();
        RelativeLayout.LayoutParams titleFrameParams = (RelativeLayout.LayoutParams) titleFrame.getLayoutParams();
        ViewGroup.LayoutParams markParams = mark.getLayoutParams();

        animator.addUpdateListener(valueAnimator -> {
            basementParams.setMargins(
                    0,
                    (Integer) valueAnimator.getAnimatedValue("margins"),
                    0,
                    (Integer) valueAnimator.getAnimatedValue("margins")
            );

            titleFrameParams.width = (int) valueAnimator.getAnimatedValue("width");
            titleFrameParams.height = (int) valueAnimator.getAnimatedValue("height");

            markParams.width = (int) valueAnimator.getAnimatedValue("height") / 2;
            markParams.height = (int) valueAnimator.getAnimatedValue("height");

            basement.setLayoutParams(basementParams);
            titleFrame.setLayoutParams(titleFrameParams);
            mark.setLayoutParams(markParams);
        });

        animator.start();

        new Handler().postDelayed(() -> {
            markParams.width = titleFrame.getHeight() / 2;
            markParams.height = titleFrame.getHeight();
            mark.setLayoutParams(markParams);
        }, 300);
    }

    private void animateDeselected() { // TODO
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(
                PropertyValuesHolder.ofInt("margins", (int) (MARGIN_SELECTED * Tools.getDensity(activity)), (int) (MARGIN_NOT_SELECTED * Tools.getDensity(activity))),
                PropertyValuesHolder.ofInt("width", titleFrame.getWidth(), getTitleWidthWrap()),
                PropertyValuesHolder.ofInt("height", titleFrame.getHeight(), (int) (titleFrame.getHeight() / 1.2f))
        );
        animator.setDuration(300);

        RecyclerView.LayoutParams basementParams = (RecyclerView.LayoutParams) basement.getLayoutParams();
        RelativeLayout.LayoutParams titleFrameParams = (RelativeLayout.LayoutParams) titleFrame.getLayoutParams();
        ViewGroup.LayoutParams markParams = mark.getLayoutParams();

        animator.addUpdateListener(valueAnimator -> {
            basementParams.setMargins(
                    0,
                    (Integer) valueAnimator.getAnimatedValue("margins"),
                    0,
                    (Integer) valueAnimator.getAnimatedValue("margins"));

            titleFrameParams.width = (int) valueAnimator.getAnimatedValue("width");
            titleFrameParams.height = (int) valueAnimator.getAnimatedValue("height");

            markParams.width = (int) valueAnimator.getAnimatedValue("height") / 2;
            markParams.height = (int) valueAnimator.getAnimatedValue("height");

            basement.setLayoutParams(basementParams);
            titleFrame.setLayoutParams(titleFrameParams);
            mark.setLayoutParams(markParams);
        });

        animator.start();

        new Handler().postDelayed(() -> {
            titleFrameParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
            titleFrameParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            titleFrame.setLayoutParams(titleFrameParams);

            new Handler().postDelayed(() -> {
                markParams.width = titleFrame.getHeight() / 2;
                markParams.height = titleFrame.getHeight();
                mark.setLayoutParams(markParams);
            }, 0);
        }, 300);
    }

    private int getTitleWidthWrap() { // TODO
        RelativeLayout titleFrame = activity.findViewById(R.id.theme_title_frame_wrap);
        return titleFrame.getWidth();
    }

    private void setupWrapParams() { // TODO
        TextView titleView = activity.findViewById(R.id.theme_title_wrap);
        titleView.setText(title.getText());
    }

    @Override
    public void updateAlphaState() { // TODO
        if (isSelected && alphaState != ALPHA_SELECTED) {
            alphaState = ALPHA_SELECTED;

        } else if (activity.selectedViewType == AbstractActivity.NO_TYPE && alphaState != ALPHA_TO_SELECT) {
            alphaState = ALPHA_TO_SELECT;

        } else if (alphaState != ALPHA_NOT_SELECTED) {
            alphaState = ALPHA_NOT_SELECTED;

        } else {
            return;
        }
        animateAlphaState();
    }

    private void animateAlphaState() { // TODO
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(basement.getAlpha(), alphaState);
        alphaAnimator.setDuration(300);

        alphaAnimator.addUpdateListener(valueAnimator -> basement.setAlpha((Float) valueAnimator.getAnimatedValue()));

        alphaAnimator.start();
    }

    @Override
    public void changeColorByAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);

        changeTitleColor(appTheme);
        changeTitleFrameColor(appTheme);
        changeMarkColor(appTheme);
    }

    private void changeTitleColor(String appTheme) {
        title.setTextColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeActiveColor :
                        R.color.darkThemeActiveColor, activity.getTheme()));
    }

    private void changeTitleFrameColor(String appTheme) {
        titleFrame.setBackgroundColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, activity.getTheme()));
    }

    @SuppressLint("DiscouragedApi")
    private void changeMarkColor(String appTheme) {
        mark.setBackgroundResource(activity.getResources().getIdentifier(
                "mark_" + appTheme,
                "drawable",
                activity.getPackageName()));
    }
}