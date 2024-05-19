package com.example.lilyasnotes.RecyclerViews;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Activities.ThemeActivity;
import com.example.lilyasnotes.Data.DTO.Data;
import com.example.lilyasnotes.Data.DTO.Note;
import com.example.lilyasnotes.Data.DTO.Theme;
import com.example.lilyasnotes.Data.ViewHolders.AbstractViewHolder;
import com.example.lilyasnotes.Data.ViewHolders.FooterViewHolder;
import com.example.lilyasnotes.Data.ViewHolders.NoteViewHolder;
import com.example.lilyasnotes.Data.ViewHolders.ThemeViewHolder;
import com.example.lilyasnotes.Database.ThemeIntoManager;
import com.example.lilyasnotes.Database.ThemeNoteManager;
import com.example.lilyasnotes.R;

import java.util.Collections;
import java.util.List;

public class ThemeRecyclerViewAdapter extends AbstractRecyclerViewAdapter {



    public ThemeRecyclerViewAdapter(ThemeActivity activity) {
        this.activity = activity;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return VIEW_TYPE_FOOTER;
        } else if (data.get(position) instanceof Theme) {
            return VIEW_TYPE_THEME;
        } else if (data.get(position) instanceof Note) {
            return VIEW_TYPE_NOTE;
        }
        return 0;
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AbstractViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_THEME:
                holder = new ThemeViewHolder(inflater.inflate(R.layout.theme_view, parent, false));
                break;
            case VIEW_TYPE_NOTE:
                holder = new NoteViewHolder(inflater.inflate(R.layout.note_view, parent, false));
                break;
            case VIEW_TYPE_FOOTER:
                holder = new FooterViewHolder(inflater.inflate(R.layout.item_management_view, parent, false));
                break;
            default:
                throw new IllegalArgumentException("Invalid view type");
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder holder, int position) {
        System.out.println("ThemeRecyclerViewAdapter onBindViewHolder");

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_THEME:
                holder.setup(((Theme) data.get(holder.getAdapterPosition())).id, activity);
                break;
            case VIEW_TYPE_NOTE:
                holder.setup(((Note) data.get(holder.getAdapterPosition())).id, activity);
                break;
            case VIEW_TYPE_FOOTER:
                holder.setup(-1, activity);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    @Override
    public void selectViewHolder(int position) {
        System.out.println("ThemeRecyclerViewAdapter selectViewHolder");

        if (data.get(position) instanceof Theme) {
            activity.selectedViewId = ((Theme) data.get(position)).id;
            activity.selectedViewType = ThemeActivity.THEME_TYPE;

        } else if (data.get(position) instanceof Note) {
            activity.selectedViewId = ((Note) data.get(position)).id;
            activity.selectedViewType = ThemeActivity.NOTE_TYPE;
        }

        AbstractViewHolder holder;
        for (int i = 0; i < getItemCount(); i++) {
            holder = (AbstractViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                holder.updateSelection();
                holder.updateAlphaState();
            }
        }
    }

    @Override
    public void deselectSelectedViewHolder() {
        System.out.println("ThemeRecyclerViewAdapter deselectSelectedViewHolder");

        activity.selectedViewId = ThemeActivity.NO_TYPE;
        activity.selectedViewType = ThemeActivity.NO_TYPE;

        AbstractViewHolder holder;
        for (int i = 0; i < getItemCount(); i++) {
            holder = (AbstractViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                holder.updateSelection();
                holder.updateAlphaState();
            }
        }
    }

    @Override
    public void onMoved(byte type, int from, int to) {
        System.out.println("onMoved");

        if (type == ThemeActivity.NO_TYPE) return;

        int id;
        if (type == ThemeActivity.THEME_TYPE)
            id = ThemeIntoManager.getThemeId(((ThemeActivity) activity).theme.id, from);
        else
            id = ThemeNoteManager.getNoteId(((ThemeActivity) activity).theme.id, from);

        int itemCount = getItemCount();
        if (from == itemCount - 1 || to == itemCount - 1) {
            return;
        }

        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(data, i, i + 1);

                if (type == ThemeActivity.THEME_TYPE)
                    { if (!activity.getSearchBar().isSearching)
                        { ThemeIntoManager.translateThemeDown(((ThemeActivity) activity).theme.id, id); } }
                else
                    { if (!activity.getSearchBar().isSearching)
                        { ThemeNoteManager.translateNoteDown(((ThemeActivity) activity).theme.id, id); } }
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(data, i, i - 1);

                if (type == ThemeActivity.THEME_TYPE)
                    { if (!activity.getSearchBar().isSearching)
                        { ThemeIntoManager.translateThemeUp(((ThemeActivity) activity).theme.id, id); } }
                else
                    { if (!activity.getSearchBar().isSearching)
                        { ThemeNoteManager.translateNoteUp(((ThemeActivity) activity).theme.id, id); } }
            }
        }
        notifyItemMoved(from, to);
    }
}