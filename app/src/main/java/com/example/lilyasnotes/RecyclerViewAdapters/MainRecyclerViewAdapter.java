package com.example.lilyasnotes.RecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.lilyasnotes.Activities.MainActivity;
import com.example.lilyasnotes.Data.DTO.Data;
import com.example.lilyasnotes.Data.DTO.Theme;
import com.example.lilyasnotes.Data.ViewHolders.DataView;
import com.example.lilyasnotes.Data.ViewHolders.FooterView;
import com.example.lilyasnotes.Data.ViewHolders.ThemeView;
import com.example.lilyasnotes.DatabaseManagement.ThemesManager;
import com.example.lilyasnotes.R;

import java.util.Collections;
import java.util.List;

public class MainRecyclerViewAdapter extends AbstractRecyclerViewAdapter {

    public MainRecyclerViewAdapter(MainActivity activity) {
        this.activity = activity;
    }

    public void setThemes(List<Data> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public DataView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_FOOTER) {
            return new FooterView(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_management_view, parent, false));
        }
        return new ThemeView(LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DataView holder, @SuppressLint("RecyclerView") int position) {
        System.out.println("MainRecyclerViewAdapter onBindViewHolder");

        if (holder instanceof FooterView) {
            holder.setup(-1, activity);
            return;
        }

        holder.setup(((Theme) data.get(holder.getAdapterPosition())).id, activity);
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
        if (activity.getUndoEraseWidget().isActive()) return;

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
