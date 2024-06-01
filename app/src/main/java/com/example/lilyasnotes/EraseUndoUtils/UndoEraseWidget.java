package com.example.lilyasnotes.EraseUndoUtils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.lilyasnotes.Activities.AbstractActivity;
import com.example.lilyasnotes.Data.DTO.Data;
import com.example.lilyasnotes.Data.DTO.Note;
import com.example.lilyasnotes.Data.DTO.Theme;
import com.example.lilyasnotes.DatabaseManagement.NoteManager;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.DatabaseManagement.ThemeManager;
import com.example.lilyasnotes.R;
import com.example.lilyasnotes.Utilities.Tools;

public class UndoEraseWidget {

    private boolean isActive;
    private Data item;
    private final AbstractActivity activity;

    private ValueAnimator progress;

    private View view;

    private final RelativeLayout basement;
    private final TextView title;
    private final RelativeLayout progressBar;

    @SuppressLint("InflateParams")
    public UndoEraseWidget(AbstractActivity activity) {
        this.activity = activity;
        view = activity.findViewById(R.id.undo_erase_container);

        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        view = inflater.inflate(R.layout.undo_erase_view, (ViewGroup) view, true);

        basement = view.findViewById(R.id.basement);
        title = view.findViewById(R.id.title);
        progressBar = view.findViewById(R.id.progress);

        isActive = false;
        basement.setEnabled(false);
        updateAlpha();
    }

    public Data getItem() {
        return item;
    }

    public boolean isActive() {
        return isActive;
    }

    public void activate(Data itemToDelete) {
        if (progress != null) {
            progress.end();
        }

        item = itemToDelete;
        isActive = true;
        basement.setEnabled(true);
        updateAlpha();

        progress = ValueAnimator.ofInt(0, basement.getWidth());
        progress.setDuration(7000);
        progress.addUpdateListener(valueAnimator -> {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
            params.width = (int) progress.getAnimatedValue();
            if ((int) progress.getAnimatedValue() <= 50 * Tools.getDensity(activity))
                { params.height = (int) progress.getAnimatedValue(); }
            else
                { params.height = basement.getHeight(); }
            progressBar.setLayoutParams(params);
        });
        progress.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {}

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                if (isActive) {
                    deleteItem();

                    basement.setEnabled(false);
                    isActive = false;
                    updateAlpha();

                    progress = null;
                }
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {
                int size = activity.data.size();

                basement.setEnabled(false);
                updateAlpha();

                activity.reloadDataComparedToSearchBar();
                notifyAdapterAboutReturn(size);
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {}
        });
        progress.start();

        basement.setOnClickListener(view -> {
            isActive = false;
            progress.cancel();
        });
    }

    private void deleteItem() {
        if (item instanceof Theme) {
            ThemeManager.deleteTheme(((Theme) item).id);
        } else if (item instanceof Note) {
            NoteManager.deleteNote(((Note) item).id);
        }
    }

    private void notifyAdapterAboutReturn(int oldSize) {
        if (activity.data.size() != oldSize) {
            for (int i = 0; i < activity.data.size(); i++) {
                Data data = activity.data.get(i);
                if (item instanceof Theme && data instanceof Theme && ((Theme) item).id == ((Theme) data).id) {
                    activity.getAdapter().notifyItemInserted(i);

                } else if (item instanceof Note && data instanceof Note && ((Note) item).id == ((Note) data).id) {
                    activity.getAdapter().notifyItemInserted(i);
                }
            }
        }
    }

    private void updateAlpha() {
        float alpha;
        if (isActive) alpha = 0.9f;
        else alpha = 0f;

        ValueAnimator alphaAnimator = ValueAnimator.ofFloat(basement.getAlpha(), alpha);
        alphaAnimator.setDuration(150);
        alphaAnimator.addUpdateListener(valueAnimator -> basement.setAlpha((Float) valueAnimator.getAnimatedValue()));
        alphaAnimator.start();

        basement.setAlpha(alpha);
    }

    public void changeColorByAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(view.getContext());

        changeBasementColor(appTheme);
        changeTitleColor(appTheme);
        changeProgressBarColor(appTheme);
    }

    @SuppressLint("DiscouragedApi")
    private void changeBasementColor(String appTheme) {
        basement.setBackgroundResource(view.getContext().getResources().getIdentifier(
                "undo_erase_frame_" + appTheme,
                "drawable",
                view.getContext().getPackageName()));
    }

    private void changeTitleColor(String appTheme) {
        title.setTextColor(view.getContext().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeActiveColor :
                        R.color.darkThemeActiveColor
        ));
    }

    private void changeProgressBarColor(String appTheme) {
        float alpha = appTheme.equals("light") ? 0.5f : 0.65f;
        progressBar.setAlpha(alpha);
    }
}
