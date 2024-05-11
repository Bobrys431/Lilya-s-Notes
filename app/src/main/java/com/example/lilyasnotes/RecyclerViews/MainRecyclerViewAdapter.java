package com.example.lilyasnotes.RecyclerViews;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Activities.AbstractActivity;
import com.example.lilyasnotes.Activities.MainActivity;
import com.example.lilyasnotes.Activities.ThemeActivity;
import com.example.lilyasnotes.Data.DTO.Data;
import com.example.lilyasnotes.Data.DTO.Theme;
import com.example.lilyasnotes.Data.ViewHolders.AbstractViewHolder;
import com.example.lilyasnotes.Data.ViewHolders.ThemeViewHolder;
import com.example.lilyasnotes.Database.ThemesManager;
import com.example.lilyasnotes.R;

import java.util.Collections;
import java.util.List;

public class MainRecyclerViewAdapter extends AbstractRecyclerViewAdapter {

    public MainRecyclerViewAdapter(MainActivity activity) {
        this.activity = activity;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setThemes(List<Data> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ThemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ThemeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder holder, @SuppressLint("RecyclerView") int position) {
        System.out.println("MainRecyclerViewAdapter onBindViewHolder");

        holder.setup(
                ((Theme) data.get(holder.getAdapterPosition())).id,
                activity,
                new AbstractViewHolder.OnTouchEvents() {
                    @Override
                    public void onSingleTapConfirmed() {
                        if (holder.isSelected)
                            { deselectSelectedViewHolder(); }
                        else
                            { selectViewHolder(holder.getAdapterPosition()); }
                        activity.getButtonsManager().updateButtonsDisplay();
                    }

                    @Override
                    public void onDoubleTap() {
                        openTheme(holder.getAdapterPosition());
                    }
                });
    }

    private void openTheme(int position) {
        Intent intent = new Intent(activity, ThemeActivity.class);
        intent.putExtra("themeId", ((Theme) data.get(position)).id);
        activity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void selectViewHolder(int position) {
        System.out.println("MainRecyclerViewAdapter selectViewHolder");

        deselectSelectedViewHolder();
        activity.selectedViewId = ((Theme) data.get(position)).id;
        activity.selectedViewType = AbstractActivity.THEME_TYPE;

        ThemeViewHolder holder;
        for (int i = 0; i < getItemCount(); i++) {
            holder = (ThemeViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                if (position == i) {
                    holder.select();
                } else {
                    holder.deselect();
                }
                holder.animateAlphaState();
            }
        }
    }

    @Override
    public void deselectSelectedViewHolder() {
        System.out.println("MainRecyclerViewAdapter deselectSelectedViewHolder");

        activity.selectedViewId = AbstractActivity.NO_TYPE;
        activity.selectedViewType = AbstractActivity.NO_TYPE;

        ThemeViewHolder holder;
        for (int i = 0; i < getItemCount(); i++) {
            holder = (ThemeViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                holder.deselect();
                holder.animateAlphaState();
            }
        }
    }

    @Override
    public void onMoved(int type, int from, int to) {
        if (type == AbstractActivity.NO_TYPE) return;

        int id = ThemesManager.getThemeId(from);

        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(data, i, i + 1);
                if (!activity.getSearchBar().isSearching)
                    { ThemesManager.translateThemeDown(id); }
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(data, i, i - 1);
                if (!activity.getSearchBar().isSearching)
                    { ThemesManager.translateThemeUp(id); }
            }
        }
        notifyItemMoved(from, to);
    }
}
