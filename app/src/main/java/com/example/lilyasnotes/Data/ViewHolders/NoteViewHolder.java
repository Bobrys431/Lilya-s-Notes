package com.example.lilyasnotes.Data.ViewHolders;

import android.animation.PropertyValuesHolder;
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
                activity.selectedViewType == ThemeActivity.NOTE_TYPE;

        setupBasement(onTouchEvents);
        setupTitle(appTheme);
        setupTitleFrame(appTheme);
        setupNote(appTheme);
        setupNoteFrame(appTheme);

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
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) basement.getLayoutParams();
        params.setMargins(0, (int) (20 * Tools.getDensity(activity)), 0, (int) (20 * Tools.getDensity(activity)));
        basement.setLayoutParams(params);
        RelativeLayout.LayoutParams frameParams = new RelativeLayout.LayoutParams(
                (int) (Tools.getDensity(activity) * 340),
                RelativeLayout.LayoutParams.WRAP_CONTENT);
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
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) basement.getLayoutParams();
        params.setMargins(0, (int) (6 * Tools.getDensity(activity)), 0, (int) (6 * Tools.getDensity(activity)));
        basement.setLayoutParams(params);
        RelativeLayout.LayoutParams frameParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
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

            setupWrapParams();
            new Handler().postDelayed(this::animateSelected, 0);
        }
    }

    public void animateSelected() {
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(
                PropertyValuesHolder.ofInt("margins", (int) (6 * Tools.getDensity(activity)), (int) (20 * Tools.getDensity(activity))),
                PropertyValuesHolder.ofFloat("alpha", 0.75f, 0.9f),
                PropertyValuesHolder.ofInt("width", titleFrame.getWidth(), (int) (Tools.getDensity(activity) * 340)),
                PropertyValuesHolder.ofInt("height", 0, getNoteHeightWrap())
        );
        animator.setDuration(300);

        RecyclerView.LayoutParams basementParams = (RecyclerView.LayoutParams) basement.getLayoutParams();
        RelativeLayout.LayoutParams titleFrameParams = (RelativeLayout.LayoutParams) titleFrame.getLayoutParams();
        RelativeLayout.LayoutParams noteFrameParams = (RelativeLayout.LayoutParams) noteFrame.getLayoutParams();

        animator.addUpdateListener(valueAnimator -> {
            basementParams.setMargins(
                    0,
                    (Integer) valueAnimator.getAnimatedValue("margins"),
                    0,
                    (Integer) valueAnimator.getAnimatedValue("margins")
            );

            basement.setAlpha((Float) valueAnimator.getAnimatedValue("alpha"));

            titleFrameParams.width = (int) valueAnimator.getAnimatedValue("width");

            noteFrameParams.width = (int) valueAnimator.getAnimatedValue("width");
            noteFrameParams.height = (int) valueAnimator.getAnimatedValue("height");

            basement.setLayoutParams(basementParams);
            titleFrame.setLayoutParams(titleFrameParams);
        });

        animator.start();

        new Handler().postDelayed(() -> {
            noteFrameParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            noteFrame.setLayoutParams(noteFrameParams);
        }, 300);
    }

    private int getNoteHeightWrap() {
        RelativeLayout noteFrame = activity.findViewById(R.id.note_text_frame_wrap);
        return noteFrame.getHeight();
    }

    @Override
    public void deselect() {
        if (isSelected) {
            isSelected = false;

            setupWrapParams();
            new Handler().postDelayed(this::animateDeselected, 0);
        }
    }

    public void animateDeselected() {
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(
                PropertyValuesHolder.ofInt("margins", (int) (20 * Tools.getDensity(activity)), (int) (6 * Tools.getDensity(activity))),
                PropertyValuesHolder.ofFloat("alpha", 0.9f, 0.75f),
                PropertyValuesHolder.ofInt("width", titleFrame.getWidth(), getTitleWidthWrap()),
                PropertyValuesHolder.ofInt("height", noteFrame.getHeight(), 0)
        );
        animator.setDuration(300);

        RecyclerView.LayoutParams basementParams = (RecyclerView.LayoutParams) basement.getLayoutParams();
        RelativeLayout.LayoutParams titleFrameParams = (RelativeLayout.LayoutParams) titleFrame.getLayoutParams();
        RelativeLayout.LayoutParams noteFrameParams = (RelativeLayout.LayoutParams) noteFrame.getLayoutParams();

        animator.addUpdateListener(valueAnimator -> {
            basementParams.setMargins(
                    0,
                    (Integer) valueAnimator.getAnimatedValue("margins"),
                    0,
                    (Integer) valueAnimator.getAnimatedValue("margins")
            );

            basement.setAlpha((Float) valueAnimator.getAnimatedValue("alpha"));

            titleFrameParams.width = (int) valueAnimator.getAnimatedValue("width");

            noteFrameParams.width = (int) valueAnimator.getAnimatedValue("width");
            noteFrameParams.height = (int) valueAnimator.getAnimatedValue("height");

            basement.setLayoutParams(basementParams);
            titleFrame.setLayoutParams(titleFrameParams);
        });

        animator.start();

        new Handler().postDelayed(() -> {
            titleFrameParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
            titleFrame.setLayoutParams(titleFrameParams);
        }, 300);
    }

    private int getTitleWidthWrap() {
        RelativeLayout titleFrame = activity.findViewById(R.id.note_title_frame_wrap);
        return titleFrame.getWidth();
    }

    private void setupWrapParams() {
        TextView titleView = activity.findViewById(R.id.note_title_wrap);
        titleView.setText(title.getText());

        TextView noteView = activity.findViewById(R.id.note_text_wrap);
        noteView.setText(note.getText());
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
