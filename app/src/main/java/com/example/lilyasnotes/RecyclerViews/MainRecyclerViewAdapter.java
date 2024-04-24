package com.example.lilyasnotes.RecyclerViews;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Activities.MainActivity;
import com.example.lilyasnotes.Activities.ThemeActivity;
import com.example.lilyasnotes.Data.DTO.Theme;
import com.example.lilyasnotes.Data.ViewHolders.DataViewHolder;
import com.example.lilyasnotes.Data.ViewHolders.ThemeViewHolder;
import com.example.lilyasnotes.Database.ThemesManager;
import com.example.lilyasnotes.R;

import java.util.Collections;
import java.util.List;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<ThemeViewHolder>
        implements RecyclerViewMoveCallback.RecyclerViewTouchHelperContract {

    MainActivity activity;
    List<Theme> themes;
    RecyclerView recyclerView;

    public MainRecyclerViewAdapter(List<Theme> themes, MainActivity activity) {
        this.activity = activity;
        this.themes = themes;
        this.recyclerView = activity.themesListView;
    }

    @NonNull
    @Override
    public ThemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ThemeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_view, parent, false));
    }

    @SuppressLint({"DiscouragedApi", "NotifyDataSetChanged", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull ThemeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setup(
                themes.get(holder.getAdapterPosition()).id,

                new DataViewHolder.OnTouchEvents() {
                    @Override
                    public void onSingleTapConfirmed() {
                        if (holder.isSelected) {
                            if (activity.getSearchBar().isSearching)
                                activity.getSearchBar().reloadData();
                            else
                                activity.reloadThemes();
                        } else {
                            selectViewHolder(holder.getAdapterPosition());
                            activity.buttonsManager.updateButtonsDisplay();
                        }
                    }

                    @Override
                    public void onDoubleTap() {
                        openTheme(holder.getAdapterPosition());
                    }
                });
    }

    private void openTheme(int position) {
        Intent intent = new Intent(activity, ThemeActivity.class);
        intent.putExtra("themeId", themes.get(position).id);
        activity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return themes.size();
    }


    public void selectViewHolder(int position) {
        deselectSelectedViewHolder();
        activity.selectedViewId = themes.get(position).id;

        ThemeViewHolder holder = (ThemeViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            holder.select();
        }
    }


    public void deselectSelectedViewHolder() {
        activity.selectedViewId = -1;

        ThemeViewHolder holder;
        for (int i = 0; i < getItemCount(); i++) {
            holder = (ThemeViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                holder.deselect();
            }
        }
    }

    @Override
    public void onMoved(int type, int from, int to) {
        if (type == ThemeActivity.NO_TYPE) return;

        int id = ThemesManager.getThemeId(from);

        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(themes, i, i + 1);
                ThemesManager.translateThemeDown(id);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(themes, i, i - 1);
                ThemesManager.translateThemeUp(id);
            }
        }
        notifyItemMoved(from, to);
    }
}
