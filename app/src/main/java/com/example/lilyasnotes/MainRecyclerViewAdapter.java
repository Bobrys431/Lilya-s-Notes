package com.example.lilyasnotes;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<Theme.ThemeViewHolder>
{

    @SuppressLint("StaticFieldLeak")
    static Context context;
    static List<Theme> themes;
    static SQLiteDatabase database;
    static RecyclerView recyclerView;

    public MainRecyclerViewAdapter(List<Theme> themes, RecyclerView recyclerView)
    {
        MainRecyclerViewAdapter.themes = themes;
        MainRecyclerViewAdapter.recyclerView = recyclerView;
        context = recyclerView.getContext();
        database = SQLiteDatabaseAdapter.getDatabase(context);
    }

    @NonNull
    @Override
    public Theme.ThemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new Theme.ThemeViewHolder(LayoutInflater.from(context).inflate(R.layout.theme_view, parent, false));
    }

    @SuppressLint({"DiscouragedApi", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull Theme.ThemeViewHolder holder, @SuppressLint("RecyclerView") int position)
    {
        final String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(context);
        assert appTheme != null : "MainRecyclerViewAdapter: appTheme = null";

        holder.title.setText(themes.get(position).title);
        holder.title.setTypeface(ResourcesCompat.getFont(context, R.font.advent_pro_bold));
        holder.title.setTextColor(context.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.black :
                        R.color.white, context.getTheme()));

        holder.titleFrame.setBackgroundColor(context.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, context.getTheme()));
        holder.titleFrame.setOnClickListener(view ->
        {
            holder.isSelected = !holder.isSelected;
            if (holder.isSelected)
            {
                boolean isSelectedViewEarlier = true;
                for (int i = 0; i < getItemCount(); i++)
                {
                    if (i != position)
                    {
                        Theme.ThemeViewHolder themeViewHolder = (Theme.ThemeViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        if (themeViewHolder != null && themeViewHolder.isSelected)
                        {
                            isSelectedViewEarlier = false;
                            themeViewHolder.isSelected = false;

                            RecyclerView.LayoutParams basementLayoutParams = (RecyclerView.LayoutParams) themeViewHolder.basement.getLayoutParams();

                            themeViewHolder.basement.setPivotX(0);
                            themeViewHolder.basement.setAlpha(0.8f);

                            ValueAnimator marginAnimator = ValueAnimator.ofInt(20, 7);
                            marginAnimator.setDuration(300);
                            marginAnimator.addUpdateListener(valueAnimator -> {
                                basementLayoutParams.setMargins(0, (int) (((int) valueAnimator.getAnimatedValue()) * Main.density), 0, (int) (((int) valueAnimator.getAnimatedValue()) * Main.density));
                                themeViewHolder.basement.setLayoutParams(basementLayoutParams);
                            });
                            marginAnimator.start();

                            ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1.3f, 1.0f);
                            scaleAnimator.setDuration(300);
                            scaleAnimator.addUpdateListener(valueAnimator -> {
                                themeViewHolder.basement.setScaleX((float) valueAnimator.getAnimatedValue());
                                themeViewHolder.basement.setScaleY((float) valueAnimator.getAnimatedValue());
                            });
                            scaleAnimator.start();

                            break;
                        }
                    }
                }
                Main.selectedViewPosition = position;

                RecyclerView.LayoutParams basementLayoutParams = (RecyclerView.LayoutParams) holder.basement.getLayoutParams();

                holder.basement.setPivotX(0);
                holder.basement.setAlpha(0.9f);

                ValueAnimator marginAnimator = ValueAnimator.ofInt(7, 20);
                marginAnimator.setDuration(300);
                marginAnimator.addUpdateListener(valueAnimator -> {
                    basementLayoutParams.setMargins(0, (int) (((int) valueAnimator.getAnimatedValue()) * Main.density), 0, (int) (((int) valueAnimator.getAnimatedValue()) * Main.density));
                    holder.basement.setLayoutParams(basementLayoutParams);
                });
                marginAnimator.start();

                ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1.0f, 1.3f);
                scaleAnimator.setDuration(300);
                scaleAnimator.addUpdateListener(valueAnimator -> {
                    holder.basement.setScaleX((float) valueAnimator.getAnimatedValue());
                    holder.basement.setScaleY((float) valueAnimator.getAnimatedValue());
                });
                scaleAnimator.start();

                if (isSelectedViewEarlier) {
                    Main.translateDeleteButton(50, 0);
                    Main.translateClickMeButton(100, -70, true);
                }
            } else
            {
                RecyclerView.LayoutParams basementLayoutParams = (RecyclerView.LayoutParams) holder.basement.getLayoutParams();

                holder.basement.setPivotX(0);
                holder.basement.setAlpha(0.8f);

                ValueAnimator marginAnimator = ValueAnimator.ofInt(15, 7);
                marginAnimator.setDuration(300);
                marginAnimator.addUpdateListener(valueAnimator -> {
                    basementLayoutParams.setMargins(0, (int) (((int) valueAnimator.getAnimatedValue()) * Main.density), 0, (int) (((int) valueAnimator.getAnimatedValue()) * Main.density));
                    holder.basement.setLayoutParams(basementLayoutParams);
                });
                marginAnimator.start();

                ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1.3f, 1.0f);
                scaleAnimator.setDuration(300);
                scaleAnimator.addUpdateListener(valueAnimator -> {
                    holder.basement.setScaleX((float) valueAnimator.getAnimatedValue());
                    holder.basement.setScaleY((float) valueAnimator.getAnimatedValue());
                });
                scaleAnimator.start();

                Main.translateDeleteButton(0, 50);
                Main.translateClickMeButton(-70, 100, false);
            }
        });

        holder.mark.setImageResource(context.getResources().getIdentifier("mark_" + appTheme, "drawable", context.getPackageName()));
        ViewGroup.LayoutParams titleFrameLayoutParams = holder.titleFrame.getLayoutParams();
        ViewGroup.LayoutParams markLayoutParams = holder.mark.getLayoutParams();

        markLayoutParams.height = titleFrameLayoutParams.height;
        markLayoutParams.width = titleFrameLayoutParams.height / 2;

        Log.d("Debug", "TitleFrame Height: " + titleFrameLayoutParams.height);
        Log.d("Debug", "Mark Height: " + markLayoutParams.height);

        holder.mark.setLayoutParams(markLayoutParams);
    }

    @Override
    public int getItemCount() {
        return themes.size();
    }
}
