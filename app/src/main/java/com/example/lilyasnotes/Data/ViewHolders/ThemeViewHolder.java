package com.example.lilyasnotes.Data.ViewHolders;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Activities.AbstractActivity;
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

    @SuppressLint("DiscouragedApi")
    public ThemeViewHolder(@NonNull View itemView) {
        super(itemView);

        basement = itemView.findViewById(R.id.basement);
        mark = itemView.findViewById(R.id.mark);
        title = itemView.findViewById(R.id.title);
        titleFrame = itemView.findViewById(R.id.title_frame);
    }

    @Override
    public void setup(int id, AbstractActivity activity, OnTouchEvents onTouchEvents) {
        final String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);
        assert appTheme != null : "ThemeViewHolder: appTheme = null";

        this.id = id;
        this.activity = activity;
        isSelected =
                activity.selectedViewId == id &&
                activity.selectedViewType == AbstractActivity.THEME_TYPE;

        setupBasement(onTouchEvents);
        setupMark(appTheme);
        setupTitle(appTheme);
        setupTitleFrame(appTheme);

        if (isSelected) {
            new Handler().postDelayed(this::visualizeLikeSelected, 350);
        } else {
            new Handler().postDelayed(this::visualizeLikeDeselected, 350);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupBasement(OnTouchEvents onTouchEvents) {
        GestureDetector gestureDetector = new GestureDetector(activity, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(@NonNull MotionEvent e) {
                onTouchEvents.onDoubleTap();
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
                onTouchEvents.onSingleTapConfirmed();
                return true;
            }
        });

        basement.setOnTouchListener((view, motionEvent) -> {
            gestureDetector.onTouchEvent(motionEvent);
            return true;
        });
    }

    @SuppressLint("DiscouragedApi")
    private void setupMark(String appTheme) {
        mark.setBackgroundResource(activity.getResources().getIdentifier(
                "mark_" + appTheme,
                "drawable",
                activity.getPackageName()));
        ViewGroup.LayoutParams titleFrameLayoutParams = titleFrame.getLayoutParams();
        ViewGroup.LayoutParams markLayoutParams = mark.getLayoutParams();

        markLayoutParams.height = titleFrameLayoutParams.height;
        markLayoutParams.width = titleFrameLayoutParams.height / 2;

        mark.setLayoutParams(markLayoutParams);
    }

    private void setupTitle(String appTheme) {
        title.setText(ThemeManager.getTitle(id));
        title.setTypeface(ResourcesCompat.getFont(activity, R.font.advent_pro_bold));
        title.setTextColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.black :
                        R.color.white, activity.getTheme()));
    }

    private void setupTitleFrame(String appTheme) {
        titleFrame.setBackgroundColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, activity.getTheme()));
    }

    @Override
    public void visualizeLikeSelected() {
        basement.setAlpha(0.9f);
        basement.setPivotX(0f);
        basement.setPivotY(50f);
        basement.setScaleX(1.35f);
        basement.setScaleY(1.35f);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) basement.getLayoutParams();
        params.setMargins(0, (int) (20 * Tools.getDensity(activity)), 0, (int) (20 * Tools.getDensity(activity)));
        basement.setLayoutParams(params);
    }

    @Override
    public void visualizeLikeDeselected() {
        basement.setAlpha(0.75f);
        basement.setPivotX(0f);
        basement.setPivotY(50f);
        basement.setScaleX(1.0f);
        basement.setScaleY(1.0f);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) basement.getLayoutParams();
        params.setMargins(0, (int) (6 * Tools.getDensity(activity)), 0, (int) (6 * Tools.getDensity(activity)));
        basement.setLayoutParams(params);
    }

    public void select() {
        if (!isSelected) {
            isSelected = true;

            animateMarginsUp();
            animateScaleUp();
            animateAlphaUp();
        }
    }

    private void animateMarginsUp() {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) basement.getLayoutParams();
        ValueAnimator marginAnimator = ValueAnimator.ofInt(6, 20);

        marginAnimator.setDuration(400);
        marginAnimator.addUpdateListener(valueAnimator -> {

            params.setMargins(
                    0,
                    (int) ((int) valueAnimator.getAnimatedValue() * Tools.getDensity(activity)),
                    0,
                    (int) ((int) valueAnimator.getAnimatedValue() * Tools.getDensity(activity)));
            basement.setLayoutParams(params);
        });

        marginAnimator.start();
    }

    private void animateScaleUp() {
        basement.setPivotX(0f);
        basement.setPivotY(50f);

        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1.0f, 1.35f);

        scaleAnimator.setDuration(400);
        scaleAnimator.addUpdateListener(valueAnimator -> {

            basement.setScaleX((float) valueAnimator.getAnimatedValue());
            basement.setScaleY((float) valueAnimator.getAnimatedValue());
        });

        scaleAnimator.start();
    }

    private void animateAlphaUp() {
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0.75f, 0.9f);

        alphaAnimator.setDuration(400);
        alphaAnimator.addUpdateListener(valueAnimator ->
                basement.setAlpha((float) valueAnimator.getAnimatedValue()));

        alphaAnimator.start();
    }

    public void deselect() {
        if (isSelected) {
            isSelected = false;

            animateMarginsDown();
            animateScaleDown();
            animateAlphaDown();
        }
    }

    private void animateMarginsDown() {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) basement.getLayoutParams();
        ValueAnimator marginAnimator = ValueAnimator.ofInt(20, 6);

        marginAnimator.setDuration(400);
        marginAnimator.addUpdateListener(valueAnimator -> {

            params.setMargins(
                    0,
                    (int) ((int) valueAnimator.getAnimatedValue() * Tools.getDensity(activity)),
                    0,
                    (int) ((int) valueAnimator.getAnimatedValue() * Tools.getDensity(activity)));
            basement.setLayoutParams(params);
        });

        marginAnimator.start();
    }

    private void animateScaleDown() {
        basement.setPivotX(0f);
        basement.setPivotY(50f);

        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1.35f, 1.0f);

        scaleAnimator.setDuration(400);
        scaleAnimator.addUpdateListener(valueAnimator -> {

            basement.setScaleX((float) valueAnimator.getAnimatedValue());
            basement.setScaleY((float) valueAnimator.getAnimatedValue());
        });

        scaleAnimator.start();
    }

    private void animateAlphaDown() {
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0.9f, 0.75f);

        alphaAnimator.setDuration(400);
        alphaAnimator.addUpdateListener(valueAnimator ->
                basement.setAlpha((float) valueAnimator.getAnimatedValue()));

        alphaAnimator.start();
    }

    @Override
    public void changeColorByAppTheme() {
        changeTitleColor();
        changeTitleFrameColor();
        changeMarkColor();
    }

    private void changeTitleColor() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);

        title.setTextColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeActiveColor :
                        R.color.darkThemeActiveColor, activity.getTheme()));
    }

    private void changeTitleFrameColor() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);

        titleFrame.setBackgroundColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, activity.getTheme()));
    }

    @SuppressLint("DiscouragedApi")
    private void changeMarkColor() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);
        System.out.println(appTheme);

        mark.setBackgroundResource(activity.getResources().getIdentifier(
                "mark_" + appTheme,
                "drawable",
                activity.getPackageName()));
    }
}