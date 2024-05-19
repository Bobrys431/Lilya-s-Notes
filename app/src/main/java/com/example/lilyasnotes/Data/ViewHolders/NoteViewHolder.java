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
    public void setup(int id, AbstractActivity activity) {
        final String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);
        assert appTheme != null : "NoteViewHolder: appTheme = null";

        this.id = id;
        this.activity = activity;
        isSelected =
                activity.selectedViewId == id &&
                activity.selectedViewType == ThemeActivity.NOTE_TYPE;

        if (isSelected) {
            alphaState = ALPHA_SELECTED;
            marginState = (int) (Tools.getDensity(activity) * MARGIN_SELECTED);

        } else if (activity.selectedViewType == AbstractActivity.NO_TYPE) {
            alphaState = ALPHA_TO_SELECT;
            marginState = (int) (Tools.getDensity(activity) * MARGIN_NOT_SELECTED);

        } else {
            alphaState = ALPHA_NOT_SELECTED;
            marginState = (int) (Tools.getDensity(activity) * MARGIN_NOT_SELECTED);
        }

        setupBasement();
        setupTitle();
        setupNote();

        changeColorByAppTheme();
        visualizeDelayed();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupBasement() {
        GestureDetector gestureDetector = new GestureDetector(activity, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
                if (isSelected)
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
        title.setText(NoteManager.getTitle(id));
        title.setTypeface(ResourcesCompat.getFont(activity, R.font.advent_pro_bold));
    }

    private void setupNote() {
        note.setText(NoteManager.getText(id));
        note.setTypeface(ResourcesCompat.getFont(activity, R.font.advent_pro_bold));

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

    private void visualizeDelayed() {
        new Handler().postDelayed(() -> {

            basement.setAlpha(alphaState);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) basement.getLayoutParams();
            params.setMargins(0, marginState, 0, marginState);
            basement.setLayoutParams(params);

            int titleWidth;
            int titleHeight;
            int noteWidth;
            int noteHeight;
            if (isSelected) {
                titleWidth = (int) (Tools.getDensity(activity) * 340);
                titleHeight = RelativeLayout.LayoutParams.WRAP_CONTENT;
                noteWidth = titleFrame.getWidth();
                noteHeight = RelativeLayout.LayoutParams.WRAP_CONTENT;
            } else {
                titleWidth = RelativeLayout.LayoutParams.WRAP_CONTENT;
                titleHeight = RelativeLayout.LayoutParams.WRAP_CONTENT;
                noteWidth = titleFrame.getWidth();
                noteHeight = 0;
            }

            RelativeLayout.LayoutParams frameParams = new RelativeLayout.LayoutParams(
                    titleWidth,
                    titleHeight);
            titleFrame.setLayoutParams(frameParams);

            new Handler().postDelayed(() -> {
                RelativeLayout.LayoutParams areaParams = new RelativeLayout.LayoutParams(
                        noteWidth,
                        noteHeight
                );
                areaParams.addRule(RelativeLayout.BELOW, R.id.title_frame);
                noteFrame.setLayoutParams(areaParams);
            }, 0);
        }, 300);
    }

    @Override
    public void updateSelection() {
        if (activity.selectedViewType == AbstractActivity.NOTE_TYPE && activity.selectedViewId == id && !isSelected) {
            isSelected = true;

            setupWrapParams();
            animateSelected();

        } else if (isSelected) {
            isSelected = false;

            setupWrapParams();
            animateDeselected();
        }
    }

    public void animateSelected() {
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(
                PropertyValuesHolder.ofInt("margins", (int) (MARGIN_NOT_SELECTED * Tools.getDensity(activity)), (int) (MARGIN_SELECTED * Tools.getDensity(activity))),
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

    public void animateDeselected() {
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(
                PropertyValuesHolder.ofInt("margins", (int) (MARGIN_SELECTED * Tools.getDensity(activity)), (int) (MARGIN_NOT_SELECTED * Tools.getDensity(activity))),
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

    public void updateAlphaState() {
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

    private void animateAlphaState() {
        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(basement.getAlpha(), alphaState);
        alphaAnimator.setDuration(300);

        alphaAnimator.addUpdateListener(valueAnimator -> basement.setAlpha((Float) valueAnimator.getAnimatedValue()));

        alphaAnimator.start();
    }

    public void changeColorByAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);

        changeTitleColor(appTheme);
        changeTitleFrameColor(appTheme);
        changeNoteColor(appTheme);
        changeNoteFrameColor(appTheme);
    }

    private void changeTitleColor(String appTheme) {
        title.setTextColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeActiveColor :
                        R.color.darkThemeActiveColor, activity.getTheme()));
    }

    @SuppressLint("DiscouragedApi")
    private void changeTitleFrameColor(String appTheme) {
        titleFrame.setBackgroundResource(activity.getResources().getIdentifier(
                "note_frame_" + appTheme,
                "drawable",
                activity.getPackageName()));
    }

    private void changeNoteColor(String appTheme) {
        note.setTextColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeActiveColor :
                        R.color.darkThemeActiveColor, activity.getTheme()));
    }

    @SuppressLint("DiscouragedApi")
    private void changeNoteFrameColor(String appTheme) {
        noteFrame.setBackgroundColor(activity.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, activity.getTheme()));
    }
}
