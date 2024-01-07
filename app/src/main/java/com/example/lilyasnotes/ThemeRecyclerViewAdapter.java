package com.example.lilyasnotes;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
