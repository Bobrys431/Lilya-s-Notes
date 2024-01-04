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

public class ThemeRecyclerViewAdapter extends RecyclerView.Adapter<DataViewHolder>
{
    final private int VIEW_TYPE_THEME = 1;
    final private int VIEW_TYPE_NOTE = 2;
    final private int VIEW_TYPE_IMAGE = 3;

    Context context;
    List<Data> data;
    SQLiteDatabase database;
    RecyclerView recyclerView;

    public ThemeRecyclerViewAdapter(List<Data> data, RecyclerView recyclerView) {
        this.data = data;
        this.recyclerView = recyclerView;
        context = recyclerView.getContext();
        database = SQLiteDatabaseAdapter.getDatabase(context);
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType)
        {
            case VIEW_TYPE_THEME:
                return new Theme.ThemeViewHolder(inflater.inflate(R.layout.theme_view, parent, false));
            case VIEW_TYPE_NOTE:
                return new Note.NoteViewHolder(inflater.inflate(R.layout.note_view, parent, false));
            case VIEW_TYPE_IMAGE:
                return new Image.ImageViewHolder(inflater.inflate(R.layout.image_view, parent, false));
            default:
                throw new IllegalArgumentException("Unknown viewType: " + viewType);
        }
    }

    @SuppressLint({"DiscouragedApi", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position)
    {
        final String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(context);
        assert appTheme != null : "ThemeRecyclerViewAdapter: appTheme = null";

        if (holder instanceof Theme.ThemeViewHolder) // ThemeViewHolder setup
        {
            Theme.ThemeViewHolder themeViewHolder = (Theme.ThemeViewHolder) holder;

            themeViewHolder.title.setText(( (Theme) data.get(position)).title);
            themeViewHolder.title.setTypeface(ResourcesCompat.getFont(context, R.font.advent_pro_bold));
            themeViewHolder.title.setTextColor(context.getResources().getColor(
                    appTheme.equals("light") ?
                            R.color.black :
                            R.color.white, context.getTheme()));

            themeViewHolder.subthemesTitles.setText(( (Theme) data.get(position)).title);
            themeViewHolder.subthemesTitles.setTypeface(ResourcesCompat.getFont(context, R.font.advent_pro_bold));
            themeViewHolder.subthemesTitles.setAlpha(0.75f);

            themeViewHolder.titleFrame.setBackgroundColor(context.getResources().getColor(
                    appTheme.equals("light") ?
                            R.color.lightThemeBackground :
                            R.color.darkThemeBackground, context.getTheme()));
            themeViewHolder.titleFrame.setOnClickListener(view ->
            {
                if (themeViewHolder.isSelected)
                {
                    if (context instanceof FragmentActivity)
                    {
                        Theme.ThemeViewDialog themeViewDialog = new Theme.ThemeViewDialog(position);
                        themeViewDialog.setOnDialogClosedListener(result ->
                        {
                            themeViewHolder.title.setText(result);
                            ( (Theme) data.get(holder.getAdapterPosition())).title = result;

                            database.execSQL("UPDATE " + SQLiteDatabaseAdapter.THEME +
                                    " SET " + SQLiteDatabaseAdapter.THEME_TITLE + " = ?" +
                                    " WHERE " + SQLiteDatabaseAdapter.THEME_ID + " = " + ( (Theme) data.get(position)).id, new Object[]{result});
                        });
                        themeViewDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "ThemeViewDialog");
                    }
                } else
                {

                    themeViewHolder.isSelected = true;
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

            themeViewHolder.dataFrame.setBackgroundColor(context.getResources().getColor(
                    appTheme.equals("light") ?
                            R.color.lightThemeBackground :
                            R.color.darkThemeBackground, context.getTheme()));
            themeViewHolder.dataFrame.setOnClickListener(view ->
            {
                if (themeViewHolder.isSelected)
                {
                    Toast.makeText(context, "CLICKED", Toast.LENGTH_SHORT).show();
                } else
                {
                    themeViewHolder.isSelected = true;
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

            for (int j = 0; j < themeViewHolder.decorations.size(); j++)
                themeViewHolder.decorations.get(j).getImageView().setImageResource(context.getResources().getIdentifier(
                        "decoration_" + themeViewHolder.decorations.get(j).getImageType() + "_" + appTheme,
                        "drawable",
                        context.getPackageName()));

            themeViewHolder.translationFrame.setBackgroundColor(context.getResources().getColor(
                    appTheme.equals("light") ?
                            R.color.lightThemeBackground :
                            R.color.darkThemeBackground, context.getTheme()));

            themeViewHolder.translationUp.setBackgroundResource(
                    appTheme.equals("light") ?
                            R.drawable.translate_light :
                            R.drawable.translate_dark);
            themeViewHolder.translationUp.setOnClickListener(view ->
            {
                if (themeViewHolder.isSelected && position != 0)
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

                    Theme themeContainer = (Theme) data.get(position - 1);
                    data.remove(position - 1);
                    data.add(position, themeContainer);

                    this.notifyDataSetChanged();
                }
            });
        } else if (holder instanceof Note.NoteViewHolder) // NoteViewHolder setup
        {
            // IN THE FUTURE
        } else if (holder instanceof Image.ImageViewHolder) // ImageViewHolder setup
        {
            // IN THE FUTURE
        }
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        Data item = data.get(position);

        if (item instanceof Theme)
        {
            return VIEW_TYPE_THEME;
        } else if (item instanceof Note)
        {
            return VIEW_TYPE_NOTE;
        } else if (item instanceof Image)
        {
            return VIEW_TYPE_IMAGE;
        }

        return -1;
    }
}
