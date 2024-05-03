package com.example.lilyasnotes.RecyclerViews;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Activities.AbstractActivity;
import com.example.lilyasnotes.Data.DTO.Data;
import com.example.lilyasnotes.Data.ViewHolders.AbstractViewHolder;

import java.util.List;

public abstract class AbstractRecyclerViewAdapter extends RecyclerView.Adapter<AbstractViewHolder>
        implements RecyclerViewMoveCallback.RecyclerViewTouchHelperContract  {

    protected AbstractActivity activity;
    protected List<Data> data;
    protected RecyclerView recyclerView;

    public abstract void selectViewHolder(int position);
    public abstract void deselectSelectedViewHolder();
}
