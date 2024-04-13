package com.example.lilyasnotes.RecyclerViews.RecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Activities.MainActivity;
import com.example.lilyasnotes.Data.DTO.Theme;
import com.example.lilyasnotes.Data.ViewHolders.DataViewHolder;
import com.example.lilyasnotes.Data.ViewHolders.ThemeViewHolder;
import com.example.lilyasnotes.R;

import java.util.List;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<ThemeViewHolder> {

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
                themes.get(position).id,

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
                        if (holder.isSelected) {
                            Toast.makeText(activity, "Clicked", Toast.LENGTH_LONG).show();
                        } else {
                            selectViewHolder(holder.getAdapterPosition());
                            Toast.makeText(activity, "Clicked", Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
}
