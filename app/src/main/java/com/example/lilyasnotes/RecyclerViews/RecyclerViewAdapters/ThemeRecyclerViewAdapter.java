package com.example.lilyasnotes.RecyclerViews.RecyclerViewAdapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Activities.ThemeActivity;
import com.example.lilyasnotes.Data.DTO.Data;
import com.example.lilyasnotes.Data.DTO.Note;
import com.example.lilyasnotes.Data.DTO.Theme;
import com.example.lilyasnotes.Data.ViewHolders.DataViewHolder;
import com.example.lilyasnotes.Data.ViewHolders.NoteViewHolder;
import com.example.lilyasnotes.Data.ViewHolders.ThemeViewHolder;
import com.example.lilyasnotes.Database.ThemeIntoManager;
import com.example.lilyasnotes.Database.ThemeNoteManager;
import com.example.lilyasnotes.R;

import java.util.List;

public class ThemeRecyclerViewAdapter extends RecyclerView.Adapter<DataViewHolder> {

    private static final int THEME_TYPE = 1;
    private static final int NOTE_TYPE = 2;

    ThemeActivity activity;
    List<Data> data;
    RecyclerView recyclerView;

    public ThemeRecyclerViewAdapter(List<Data> data, ThemeActivity activity) {
        this.activity = activity;
        this.data = data;
        this.recyclerView = activity.dataListView;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) instanceof Theme) {
            return THEME_TYPE;
        } else if (data.get(position) instanceof Note) {
            return NOTE_TYPE;
        }
        return 0;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DataViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case THEME_TYPE:
                holder = new ThemeViewHolder(inflater.inflate(R.layout.theme_view, parent, false));
                break;
            case NOTE_TYPE:
                holder = new NoteViewHolder(inflater.inflate(R.layout.note_view, parent, false));
                break;
            default:
                throw new IllegalArgumentException("Invalid view type");
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case THEME_TYPE:
                holder.setup(((Theme) data.get(holder.getAdapterPosition())).id, new DataViewHolder.OnTouchEvents() {
                    @Override
                    public void onSingleTapConfirmed() {
                        if (holder.isSelected) {
                            if (activity.getSearchBar().isSearching)
                                activity.getSearchBar().reloadData();
                            else
                                activity.reloadData();
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
                break;
            case NOTE_TYPE:
                holder.setup(((Note) data.get(holder.getAdapterPosition())).id, new DataViewHolder.OnTouchEvents() {
                    @Override
                    public void onSingleTapConfirmed() {
                        if (holder.isSelected) {
                            if (activity.getSearchBar().isSearching)
                                activity.getSearchBar().reloadData();
                            else
                                activity.reloadData();
                        } else {
                            selectViewHolder(holder.getAdapterPosition());
                            activity.buttonsManager.updateButtonsDisplay();
                        }
                    }

                    @Override
                    public void onDoubleTap() {
                        if (holder.isSelected) {
                            if (activity.getSearchBar().isSearching)
                                activity.getSearchBar().reloadData();
                            else
                                activity.reloadData();
                        } else {
                            selectViewHolder(holder.getAdapterPosition());
                            activity.buttonsManager.updateButtonsDisplay();
                        }
                    }
                });
                break;
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
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

    public void selectViewHolder(int position) {
        deselectSelectedViewHolder();

        if (data.get(position) instanceof Theme) {
            activity.selectedViewId = ((Theme) data.get(position)).id;
            activity.selectedViewType = ThemeActivity.THEME_TYPE;
            System.out.println(ThemeIntoManager.getParentId(((Theme) data.get(position)).id));

        } else if (data.get(position) instanceof Note) {
            activity.selectedViewId = ((Note) data.get(position)).id;
            activity.selectedViewType = ThemeActivity.NOTE_TYPE;
            System.out.println(ThemeNoteManager.getParentId(((Note) data.get(position)).id));
        }

        DataViewHolder holder = (DataViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            holder.select();
        }
    }


    public void deselectSelectedViewHolder() {
        activity.selectedViewId = ThemeActivity.NO_TYPE;
        activity.selectedViewType = ThemeActivity.NO_TYPE;

        DataViewHolder holder;
        for (int i = 0; i < getItemCount(); i++) {
            holder = (DataViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                holder.deselect();
            }
        }
    }
}
