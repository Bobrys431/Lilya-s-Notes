package com.example.lilyasnotes.Data.ViewHolders;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Activities.AbstractActivity;
import com.example.lilyasnotes.Activities.ThemeActivity;
import com.example.lilyasnotes.Database.NoteManager;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;
import com.example.lilyasnotes.Utilities.Tools;

public class NoteViewHolder extends AbstractViewHolder {

    private int id;

    public RelativeLayout basement;
    public TextView title;
    public RelativeLayout titleFrame;
    public TextView note;
    public RelativeLayout noteFrame;

    public NoteViewHolder(@NonNull View itemView) {
        super(itemView);

        basement = itemView.findViewById(R.id.basement);
        title = itemView.findViewById(R.id.title);
        titleFrame = itemView.findViewById(R.id.title_frame);
        note = itemView.findViewById(R.id.note);
        noteFrame = itemView.findViewById(R.id.note_frame);
    }

    @Override
    public void setup(int id, AbstractActivity activity, OnTouchEvents onTouchEvents) {
        final String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);
        assert appTheme != null : "NoteViewHolder: appTheme = null";

        this.id = id;
        this.activity = activity;
        isSelected =
                activity.selectedViewId == id &&
                activity.selectedViewType == ThemeActivity.NOTE_TYPE; // TODO Допиши перевірку на selectedViewType

        setupBasement(onTouchEvents);
        setupTitle(appTheme);
        setupTitleFrame(appTheme);
        setupNote(appTheme);
        setupNoteFrame(appTheme);

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

    private void setupTitle(String appTheme) {
        title.setText(NoteManager.getTitle(id));
        title.setTypeface(ResourcesCompat.getFont(activity, R.font.advent_pro_bold));
        title.setTextColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.black :
                        R.color.white, activity.getTheme()));
    }

    @SuppressLint("DiscouragedApi")
    private void setupTitleFrame(String appTheme) {
        titleFrame.setBackgroundResource(activity.getResources().getIdentifier(
                "note_frame_" + appTheme,
                "drawable",
                activity.getPackageName()));
    }

    private void setupNote(String appTheme) {
        note.setText(NoteManager.getText(id));
        note.setTypeface(ResourcesCompat.getFont(activity, R.font.advent_pro_bold));
        note.setTextColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.black :
                        R.color.white, activity.getTheme()));

        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { /* **NOTHING** */ }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                NoteManager.setText(id, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) { /* **NOTHING** */ }
        });
    }

    private void setupNoteFrame(String appTheme) {
        noteFrame.setBackgroundColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, activity.getTheme()));
    }

    @Override
    public void visualizeLikeSelected() {
        basement.setAlpha(0.9f);
        basement.setPivotY(50f);
        basement.setScaleY(1.35f);
        title.setTextScaleX(1.35f);
        note.setTextScaleX(1.35f);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) basement.getLayoutParams();
        params.setMargins(0, (int) (20 * Tools.getDensity(activity)), 0, (int) (6.5f * note.getLineCount() * Tools.getDensity(activity)));
        basement.setLayoutParams(params);
        RelativeLayout.LayoutParams frameParams = new RelativeLayout.LayoutParams(
                (int) (Tools.getDensity(activity) * 300),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        titleFrame.setLayoutParams(frameParams);
        new Handler().postDelayed(() -> {
            RelativeLayout.LayoutParams areaParams = new RelativeLayout.LayoutParams(
                    titleFrame.getWidth(),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            areaParams.addRule(RelativeLayout.BELOW, R.id.title_frame);
            noteFrame.setLayoutParams(areaParams);
        }, 0);
    }

    @Override
    public void visualizeLikeDeselected() {
        basement.setAlpha(0.75f);
        basement.setPivotY(50f);
        basement.setScaleY(1.0f);
        title.setTextScaleX(1.0f);
        note.setTextScaleX(1.0f);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) basement.getLayoutParams();
        params.setMargins(0, (int) (6 * Tools.getDensity(activity)), 0, (int) (6 * Tools.getDensity(activity)));
        basement.setLayoutParams(params);
        RelativeLayout.LayoutParams frameParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        titleFrame.setLayoutParams(frameParams);
        new Handler().postDelayed(() -> {
            RelativeLayout.LayoutParams areaParams = new RelativeLayout.LayoutParams(
                    titleFrame.getWidth(),
                    0
            );
            areaParams.addRule(RelativeLayout.BELOW, R.id.title_frame);
            noteFrame.setLayoutParams(areaParams);
        }, 0);
    }

    @Override
    public void select() {
        if (!isSelected) {
            isSelected = true;

            animateMarginsUp();
            animateScaleUp();
            animateAlphaUp();

            animateTitleWidthUp();
            new Handler().postDelayed(this::animateNoteAreaUp, 550);
        }
    }

    private void animateMarginsUp() {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) basement.getLayoutParams();
        ValueAnimator topAnimator = ValueAnimator.ofFloat(6, 20);
        ValueAnimator bottomAnimator = ValueAnimator.ofFloat(6, 6.5f);
        topAnimator.setDuration(400);
        bottomAnimator.setDuration(400);

        topAnimator.addUpdateListener(valueAnimator -> {
            params.topMargin = (int) ((float) valueAnimator.getAnimatedValue() * Tools.getDensity(activity));
            basement.setLayoutParams(params);
        });
        bottomAnimator.addUpdateListener(valueAnimator -> {
            params.bottomMargin = (int) ((float) valueAnimator.getAnimatedValue() * note.getLineCount() * Tools.getDensity(activity));
            basement.setLayoutParams(params);
        });

        topAnimator.start();
        bottomAnimator.start();
    }

    private void animateScaleUp() {
        basement.setPivotY(50f);

        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1.0f, 1.35f);

        scaleAnimator.setDuration(400);
        scaleAnimator.addUpdateListener(valueAnimator -> {
            basement.setScaleY((float) valueAnimator.getAnimatedValue());
            title.setTextScaleX((float) valueAnimator.getAnimatedValue());
            note.setTextScaleX((float) valueAnimator.getAnimatedValue());
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

    private void animateTitleWidthUp() {
        ValueAnimator widthAnimator = ValueAnimator.ofFloat(titleFrame.getWidth(), Tools.getDensity(activity) * 300);
        widthAnimator.setDuration(550);

        widthAnimator.addUpdateListener(valueAnimator -> {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    (int) ((float) valueAnimator.getAnimatedValue()),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            titleFrame.setLayoutParams(layoutParams);
        });

        widthAnimator.start();
    }

    private void animateNoteAreaUp() {
        RelativeLayout.LayoutParams wrapParams = new RelativeLayout.LayoutParams(
                titleFrame.getWidth(),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        noteFrame.setLayoutParams(wrapParams);

        new Handler().postDelayed(() -> {
            ValueAnimator areaAnimator = ValueAnimator.ofFloat(0, noteFrame.getHeight());
            areaAnimator.setDuration(550);

            areaAnimator.addUpdateListener(valueAnimator -> {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        titleFrame.getWidth(),
                        (int) ((float) valueAnimator.getAnimatedValue())
                );
                layoutParams.addRule(RelativeLayout.BELOW, R.id.title_frame);
                noteFrame.setLayoutParams(layoutParams);
            });

            areaAnimator.start();

            new Handler().postDelayed(() -> {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        titleFrame.getWidth(),
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                layoutParams.addRule(RelativeLayout.BELOW, R.id.title_frame);
                noteFrame.setLayoutParams(layoutParams);
            }, 550);
        }, 0);
    }

    @Override
    public void deselect() {
        if (isSelected) {
            isSelected = false;

            animateMarginsDown();
            animateScaleDown();
            animateAlphaDown();

            new Handler().postDelayed(this::animateTitleWidthDown, 550);
            animateNoteAreaDown();
        }
    }

    private void animateMarginsDown() {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) basement.getLayoutParams();
        ValueAnimator topAnimator = ValueAnimator.ofInt(20, 6);
        ValueAnimator bottomAnimator = ValueAnimator.ofInt(40, 6);
        topAnimator.setDuration(400);
        bottomAnimator.setDuration(400);

        topAnimator.addUpdateListener(valueAnimator -> {
            params.topMargin = (int) ((int) valueAnimator.getAnimatedValue() * Tools.getDensity(activity));
            basement.setLayoutParams(params);
        });
        bottomAnimator.addUpdateListener(valueAnimator -> {
            params.bottomMargin = (int) ((int) valueAnimator.getAnimatedValue() * Tools.getDensity(activity));
            basement.setLayoutParams(params);
        });

        topAnimator.start();
        bottomAnimator.start();
    }

    private void animateScaleDown() {
        basement.setPivotY(50f);

        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1.35f, 1.0f);

        scaleAnimator.setDuration(400);
        scaleAnimator.addUpdateListener(valueAnimator -> {
            basement.setScaleY((float) valueAnimator.getAnimatedValue());
            title.setTextScaleX((float) valueAnimator.getAnimatedValue());
            note.setTextScaleX((float) valueAnimator.getAnimatedValue());
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

    private void animateTitleWidthDown() {
        RelativeLayout.LayoutParams wrapParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        titleFrame.setLayoutParams(wrapParams);

        new Handler().postDelayed(() -> {
            ValueAnimator widthAnimator = ValueAnimator.ofFloat(Tools.getDensity(activity) * 300, titleFrame.getWidth());
            widthAnimator.setDuration(550);

            widthAnimator.addUpdateListener(valueAnimator -> {
                RelativeLayout.LayoutParams secondLayoutParams = new RelativeLayout.LayoutParams(
                        (int) ((float) valueAnimator.getAnimatedValue()),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                titleFrame.setLayoutParams(secondLayoutParams);
            });

            widthAnimator.start();

            new Handler().postDelayed(() -> {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                titleFrame.setLayoutParams(layoutParams);
            }, 550);
        }, 0);
    }

    private void animateNoteAreaDown() {
        ValueAnimator areaAnimator = ValueAnimator.ofFloat(noteFrame.getHeight(), 0);
        areaAnimator.setDuration(550);

        areaAnimator.addUpdateListener(valueAnimator -> {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    titleFrame.getWidth(),
                    (int) ((float) valueAnimator.getAnimatedValue())
            );
            layoutParams.addRule(RelativeLayout.BELOW, R.id.title_frame);
            noteFrame.setLayoutParams(layoutParams);
        });

        areaAnimator.start();
    }

    @Override
    public void changeColorByAppTheme() {
        changeTitleColor();
        changeTitleFrameColor();
        changeNoteColor();
        changeNoteFrameColor();
    }

    private void changeTitleColor() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);

        title.setTextColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeActiveColor :
                        R.color.darkThemeActiveColor, activity.getTheme()));
    }

    @SuppressLint("DiscouragedApi")
    private void changeTitleFrameColor() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);

        titleFrame.setBackgroundResource(activity.getResources().getIdentifier(
                "note_frame_" + appTheme,
                "drawable",
                activity.getPackageName()));
    }

    private void changeNoteColor() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);

        note.setTextColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeActiveColor :
                        R.color.darkThemeActiveColor, activity.getTheme()));
    }

    @SuppressLint("DiscouragedApi")
    private void changeNoteFrameColor() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);

        noteFrame.setBackgroundColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, activity.getTheme()));
    }
}
