package com.example.lilyasnotes.Data.ViewHolders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Activities.AbstractActivity;

public abstract class AbstractViewHolder extends RecyclerView.ViewHolder {

    final protected static float ALPHA_NOT_SELECTED = 0.5f;
    final protected static float ALPHA_TO_SELECT = 0.75f;
    final protected static float ALPHA_SELECTED = 0.9f;

    final protected static int MARGIN_NOT_SELECTED = 6;
    final protected static int MARGIN_SELECTED = 12;

    protected float alphaState;
    protected int marginState;

    public boolean isSelected;
    protected AbstractActivity activity;

    public AbstractViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void setup(int id, AbstractActivity activity);

    public abstract void updateSelection();

    public abstract void updateAlphaState();

    public abstract void changeColorByAppTheme();
}
