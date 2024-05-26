package com.example.lilyasnotes.Data.ViewHolders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Activities.AbstractActivity;

public abstract class AbstractViewHolder extends RecyclerView.ViewHolder {
    protected AbstractActivity activity;

    public AbstractViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void setup(int id, AbstractActivity activity);

    public abstract void changeColorByAppTheme();
}
