package com.example.lilyasnotes.Data.ViewHolders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.example.lilyasnotes.Activities.AbstractActivity;
import com.example.lilyasnotes.Activities.ThemeActivity;
import com.example.lilyasnotes.Database.ThemeManager;
import com.example.lilyasnotes.R;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;

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

        setupBasement();
        setupTitle();
        setupMark();

        changeColorByAppTheme();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupBasement() {
        basement.setOnClickListener(view -> {
            Intent intent = new Intent(activity, ThemeActivity.class);
            intent.putExtra("themeId", id);
            activity.startActivity(intent);
        });
    }

    private void setupTitle() {
        title.setText(ThemeManager.getTitle(id));
        title.setTypeface(ResourcesCompat.getFont(activity, R.font.advent_pro_bold));
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