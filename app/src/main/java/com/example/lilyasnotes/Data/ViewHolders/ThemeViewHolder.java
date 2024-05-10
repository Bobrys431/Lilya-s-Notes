package com.example.lilyasnotes.Data.ViewHolders;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
            new Handler().postDelayed(this::visualizeLikeSelected, 300);
        } else {
            new Handler().postDelayed(this::visualizeLikeDeselected, 300);
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

        ViewTreeObserver vto = titleFrame.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                titleFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int height = titleFrame.getHeight();

                ViewGroup.LayoutParams markLayoutParams = mark.getLayoutParams();
                markLayoutParams.height = height;
                markLayoutParams.width = height / 2;
                mark.setLayoutParams(markLayoutParams);
            }
        });
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
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) basement.getLayoutParams();
        params.setMargins(0, (int) (20 * Tools.getDensity(activity)), 0, (int) (20 * Tools.getDensity(activity)));
        basement.setLayoutParams(params);
        RelativeLayout.LayoutParams frameParams = new RelativeLayout.LayoutParams(
                (int) (Tools.getDensity(activity) * 290),
                (int) (titleFrame.getHeight() * 1.1f));
        titleFrame.setLayoutParams(frameParams);
    }

    @Override
    public void visualizeLikeDeselected() {
        basement.setAlpha(0.75f);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) basement.getLayoutParams();
        params.setMargins(0, (int) (6 * Tools.getDensity(activity)), 0, (int) (6 * Tools.getDensity(activity)));
        basement.setLayoutParams(params);
        RelativeLayout.LayoutParams frameParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleFrame.setLayoutParams(frameParams);
    }

    public void select() {
        if (!isSelected) {
            isSelected = true;

            setupWrapParams();
            animateSelected();
        }
    }

    private void animateSelected() {
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(
                PropertyValuesHolder.ofInt("margins", (int) (6 * Tools.getDensity(activity)), (int) (20 * Tools.getDensity(activity))),
                PropertyValuesHolder.ofFloat("alpha", 0.75f, 0.9f),
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

            basement.setAlpha((Float) valueAnimator.getAnimatedValue("alpha"));

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

    public void deselect() {
        if (isSelected) {
            isSelected = false;

            setupWrapParams();
            animateDeselected();
        }
    }

    private void animateDeselected() {
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(
                PropertyValuesHolder.ofInt("margins", (int) (20 * Tools.getDensity(activity)), (int) (6 * Tools.getDensity(activity))),
                PropertyValuesHolder.ofFloat("alpha", 0.9f, 0.75f),
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

            basement.setAlpha((Float) valueAnimator.getAnimatedValue("alpha"));

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

    private int getTitleWidthWrap() {
        RelativeLayout titleFrame = activity.findViewById(R.id.theme_title_frame_wrap);
        return titleFrame.getWidth();
    }

    private void setupWrapParams() {
        TextView titleView = activity.findViewById(R.id.theme_title_wrap);
        titleView.setText(title.getText());
    }

    @SuppressLint("DiscouragedApi")
    @Override
    public void changeColorByAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);

        title.setTextColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeActiveColor :
                        R.color.darkThemeActiveColor, activity.getTheme()));

        titleFrame.setBackgroundColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, activity.getTheme()));

        mark.setBackgroundResource(activity.getResources().getIdentifier(
                "mark_" + appTheme,
                "drawable",
                activity.getPackageName()));
    }
}