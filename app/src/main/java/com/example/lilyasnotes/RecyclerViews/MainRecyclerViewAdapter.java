package com.example.lilyasnotes.RecyclerViews;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Activities.MainActivity;
import com.example.lilyasnotes.Data.DTO.Data;
import com.example.lilyasnotes.Data.DTO.Theme;
import com.example.lilyasnotes.Data.ViewHolders.AbstractViewHolder;
import com.example.lilyasnotes.Data.ViewHolders.FooterViewHolder;
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
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_FOOTER) {
            return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_management_view, parent, false));
        }
        return new ThemeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder holder, @SuppressLint("RecyclerView") int position) {
        System.out.println("MainRecyclerViewAdapter onBindViewHolder");

        if (holder instanceof FooterViewHolder) {
            holder.setup(-1, activity);
            return;
        }

        holder.setup(((Theme) data.get(holder.getAdapterPosition())).id, activity);
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return VIEW_TYPE_FOOTER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onMoved(byte type, int from, int to) {
        System.out.println("onMoved");

        if (type == AbstractRecyclerViewAdapter.VIEW_TYPE_FOOTER) return;

        int id = ThemesManager.getThemeId(from);

        int itemCount = getItemCount();
        if (from == itemCount - 1 || to == itemCount - 1) {
            return;
        }

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
