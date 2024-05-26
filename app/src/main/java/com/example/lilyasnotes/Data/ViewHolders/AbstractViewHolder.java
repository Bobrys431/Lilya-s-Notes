package com.example.lilyasnotes.Data.ViewHolders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Activities.AbstractActivity;

public abstract class AbstractViewHolder extends RecyclerView.ViewHolder {

    final protected static float ALPHA_NOT_SELECTED = 0.5f; // TODO
    final protected static float ALPHA_TO_SELECT = 0.75f; // TODO
    final protected static float ALPHA_SELECTED = 0.9f; // TODO

    final protected static int MARGIN_NOT_SELECTED = 6; // TODO
    final protected static int MARGIN_SELECTED = 12; // TODO

    protected float alphaState; // TODO
    protected int marginState; // TODO

    public boolean isSelected; // TODO
    protected AbstractActivity activity;

    public AbstractViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void setup(int id, AbstractActivity activity);

    public abstract void updateSelection(); // TODO

    public abstract void updateAlphaState(); // TODO

    public abstract void changeColorByAppTheme();
}
