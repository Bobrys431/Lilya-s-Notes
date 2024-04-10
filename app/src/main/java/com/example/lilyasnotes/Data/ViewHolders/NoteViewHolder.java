package com.example.lilyasnotes.Data.ViewHolders;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Database.NoteManager;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;
import com.example.lilyasnotes.Utilities.Tools;

public class NoteViewHolder extends DataViewHolder {

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
    public void setup(int id, OnTouchEvents onTouchEvents) {
        final String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(context);
        assert appTheme != null : "NoteViewHolder: appTheme = null";

        setupBasement(onTouchEvents);
        setupTitle(id, appTheme);
        setupTitleFrame(appTheme);
        setupNote(id, appTheme);
        setupNoteFrame(appTheme);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupBasement(OnTouchEvents onTouchEvents) {
        GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
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

    private void setupTitle(int id, String appTheme) {
        title.setText(NoteManager.getTitle(id));
        title.setTypeface(ResourcesCompat.getFont(context, R.font.advent_pro_bold));
        title.setTextColor(context.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.black :
                        R.color.white, context.getTheme()));
    }

    @SuppressLint("DiscouragedApi")
    private void setupTitleFrame(String appTheme) {
        titleFrame.setBackgroundResource(context.getResources().getIdentifier(
                "note_frame_" + appTheme,
                "drawable",
                context.getPackageName()));
    }

    private void setupNote(int id, String appTheme) {
        note.setText(NoteManager.getText(id));
        note.setTypeface(ResourcesCompat.getFont(context, R.font.advent_pro_bold));
        note.setTextColor(context.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.black :
                        R.color.white, context.getTheme()));
    }

    private void setupNoteFrame(String appTheme) {
        noteFrame.setBackgroundColor(context.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, context.getTheme()));
    }

    @Override
    public void select() {
        if (!isSelected) {
            isSelected = true;

            animateMarginsUp();
            animateScaleUp();
            animateAlphaUp();
            showNoteFrame();
        }
    }

    private void animateMarginsUp() {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) basement.getLayoutParams();
        ValueAnimator marginAnimator = ValueAnimator.ofInt(6, 20);

        marginAnimator.setDuration(400);
        marginAnimator.addUpdateListener(valueAnimator -> {

            params.setMargins(
                    0,
                    (int) ((int) valueAnimator.getAnimatedValue() * Tools.getDensity(context)),
                    0,
                    (int) ((int) valueAnimator.getAnimatedValue() * Tools.getDensity(context)));
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

    private void showNoteFrame() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) noteFrame.getLayoutParams();
        params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        noteFrame.setLayoutParams(params);
    }

    @Override
    public void deselect() {
        if (isSelected) {
            isSelected = false;

            animateMarginsDown();
            animateScaleDown();
            animateAlphaDown();
            hideNoteFrame();
        }
    }

    private void animateMarginsDown() {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) basement.getLayoutParams();
        ValueAnimator marginAnimator = ValueAnimator.ofInt(20, 6);

        marginAnimator.setDuration(400);
        marginAnimator.addUpdateListener(valueAnimator -> {

            params.setMargins(
                    0,
                    (int) ((int) valueAnimator.getAnimatedValue() * Tools.getDensity(context)),
                    0,
                    (int) ((int) valueAnimator.getAnimatedValue() * Tools.getDensity(context)));
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

    private void hideNoteFrame() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) noteFrame.getLayoutParams();
        params.height = 0;
        noteFrame.setLayoutParams(params);
    }

    @Override
    public void changeViewHolderColorToAppTheme() {
        changeTitleColor();
        changeTitleFrameColor();
        changeNoteColor();
        changeNoteFrameColor();
    }

    private void changeTitleColor() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(context);

        title.setTextColor(context.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.black :
                        R.color.white, context.getTheme()));
    }

    @SuppressLint("DiscouragedApi")
    private void changeTitleFrameColor() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(context);

        titleFrame.setBackgroundResource(context.getResources().getIdentifier(
                "note_frame_" + appTheme,
                "drawable",
                context.getPackageName()));
    }

    private void changeNoteColor() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(context);

        note.setTextColor(context.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.black :
                        R.color.white, context.getTheme()));
    }

    @SuppressLint("DiscouragedApi")
    private void changeNoteFrameColor() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(context);

        noteFrame.setBackgroundColor(context.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, context.getTheme()));
    }
}
