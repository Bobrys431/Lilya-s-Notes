package com.example.lilyasnotes.RecyclerViewAdapters;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Activities.AbstractActivity;
import com.example.lilyasnotes.Data.DTO.Data;
import com.example.lilyasnotes.Data.ViewHolders.AbstractViewHolder;

import java.util.List;

public abstract class AbstractRecyclerViewAdapter extends RecyclerView.Adapter<AbstractViewHolder>
        implements RecyclerViewMoveCallback.TouchHelperContract {

    protected static final int VIEW_TYPE_FOOTER = -1;
    protected static final int VIEW_TYPE_THEME = -2;
    protected static final int VIEW_TYPE_NOTE = -3;

    protected AbstractActivity activity;
    protected List<Data> data;
    protected RecyclerView recyclerView;
}
