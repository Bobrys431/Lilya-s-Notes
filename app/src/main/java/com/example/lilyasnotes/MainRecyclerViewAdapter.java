package com.example.lilyasnotes;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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
    public void onBindViewHolder(@NonNull Theme.ThemeViewHolder holder, int position)
    {
        final String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(context);
        assert appTheme != null : "MainRecyclerViewAdapter: appTheme = null";

        holder.title.setText(themes.get(position).title);
        holder.title.setTypeface(ResourcesCompat.getFont(context, R.font.advent_pro_bold));
        holder.title.setTextColor(context.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.black :
                        R.color.white, context.getTheme()));

        holder.subthemesTitles.setText(themes.get(position).title);
        holder.subthemesTitles.setTypeface(ResourcesCompat.getFont(context, R.font.advent_pro_bold));
        holder.subthemesTitles.setAlpha(0.75f);

        holder.titleFrame.setBackgroundColor(context.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, context.getTheme()));
        holder.titleFrame.setOnClickListener(view ->
        {
            if (holder.isSelected)
            {
                if (context instanceof FragmentActivity)
                {
                    Theme.ThemeViewDialog themeViewDialog = new Theme.ThemeViewDialog(position);
                    themeViewDialog.setOnDialogClosedListener(result ->
                    {
                        holder.title.setText(result);
                        themes.get(holder.getAdapterPosition()).title = result;

                        database.execSQL("UPDATE " + SQLiteDatabaseAdapter.THEME +
                                " SET " + SQLiteDatabaseAdapter.THEME_TITLE + " = ?" +
                                " WHERE " + SQLiteDatabaseAdapter.THEME_ID + " = " + themes.get(position).id, new Object[]{result});
                    });
                    themeViewDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "ThemeViewDialog");
                }
            } else
            {

                holder.isSelected = true;
                LinearLayoutManager layoutManager;
                View clickedView;
                if ((layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager()) != null && (clickedView = layoutManager.findViewByPosition(position)) != null)
                {
                    float targetAlpha = 0.85f;
                    ObjectAnimator.ofFloat(clickedView, "alpha", clickedView.getAlpha(), targetAlpha).setDuration(400).start();

                    float targetScale = 1.0f;
                    ObjectAnimator.ofFloat(clickedView, "scaleX", clickedView.getScaleX(), targetScale).setDuration(400).start();
                    ObjectAnimator.ofFloat(clickedView, "scaleY", clickedView.getScaleY(), targetScale).setDuration(400).start();
                }
            }
        });

        holder.dataFrame.setBackgroundColor(context.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, context.getTheme()));
        holder.dataFrame.setOnClickListener(view ->
        {
            if (holder.isSelected)
            {
                Toast.makeText(context, "CLICKED", Toast.LENGTH_SHORT).show();
            } else
            {
                holder.isSelected = true;
                LinearLayoutManager layoutManager;
                View clickedView;
                if (
                        (layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager()) != null &&
                        (clickedView = layoutManager.findViewByPosition(position)) != null
                )
                {
                    float targetAlpha = 0.85f;
                    ObjectAnimator.ofFloat(clickedView, "alpha", clickedView.getAlpha(), targetAlpha).setDuration(400).start();

                    float targetScale = 1.0f;
                    ObjectAnimator.ofFloat(clickedView, "scaleX", clickedView.getScaleX(), targetScale).setDuration(400).start();
                    ObjectAnimator.ofFloat(clickedView, "scaleY", clickedView.getScaleY(), targetScale).setDuration(400).start();
                }
            }
        });

        for (int j = 0; j < holder.decorations.size(); j++)
            holder.decorations.get(j).getImageView().setImageResource(context.getResources().getIdentifier(
                    "decoration_" + holder.decorations.get(j).getImageType() + "_" + appTheme,
                    "drawable",
                    context.getPackageName()));

        holder.translationFrame.setBackgroundColor(context.getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, context.getTheme()));

        holder.translationUp.setBackgroundResource(
                appTheme.equals("light") ?
                        R.drawable.translate_light :
                        R.drawable.translate_dark);
        holder.translationUp.setOnClickListener(view ->
        {
            if (holder.isSelected && position != 0)
            {
                Cursor themeToSwap =  database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEMES_THEME_ID +
                        " FROM " + SQLiteDatabaseAdapter.THEMES +
                        " WHERE " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " = " + position + " - 1", null);

                if (themeToSwap != null && themeToSwap.moveToNext())
                {
                    database.execSQL("UPDATE " + SQLiteDatabaseAdapter.THEMES +
                            " SET " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " = " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " - 1" +
                            " WHERE " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " = " + position);

                    database.execSQL("UPDATE " + SQLiteDatabaseAdapter.THEMES +
                            " SET " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " = " + position +
                            " WHERE " + SQLiteDatabaseAdapter.THEMES_THEME_ID + " = " + themeToSwap.getInt(themeToSwap.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEMES_THEME_ID)));

                    themeToSwap.close();
                }

                Theme themeContainer = themes.get(position - 1);
                themes.remove(position - 1);
                themes.add(position, themeContainer);

                this.notifyDataSetChanged();
            }
        });

        holder.translationDown.setBackgroundResource(
                appTheme.equals("light") ?
                        R.drawable.translate_light :
                        R.drawable.translate_dark);
        holder.translationDown.setOnClickListener(view ->
        {
            if (holder.isSelected && position != themes.size() - 1)
            {
                Cursor themeToSwap =  database.rawQuery("SELECT " + SQLiteDatabaseAdapter.THEMES_THEME_ID +
                        " FROM " + SQLiteDatabaseAdapter.THEMES +
                        " WHERE " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " = " + position + " + 1", null);

                if (themeToSwap != null && themeToSwap.moveToNext())
                {
                    database.execSQL("UPDATE " + SQLiteDatabaseAdapter.THEMES +
                            " SET " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " = " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " + 1" +
                            " WHERE " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " = " + position);

                    database.execSQL("UPDATE " + SQLiteDatabaseAdapter.THEMES +
                            " SET " + SQLiteDatabaseAdapter.THEMES_THEME_INDEX + " = " + position +
                            " WHERE " + SQLiteDatabaseAdapter.THEMES_THEME_ID + " = " + themeToSwap.getInt(themeToSwap.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEMES_THEME_ID)));

                    themeToSwap.close();
                }

                Theme themeContainer = themes.get(position + 1);
                themes.remove(position + 1);
                themes.add(position, themeContainer);

                this.notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return themes.size();
    }
}
