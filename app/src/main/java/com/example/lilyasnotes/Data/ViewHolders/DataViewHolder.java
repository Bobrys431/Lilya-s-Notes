package com.example.lilyasnotes.Data.ViewHolders;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class DataViewHolder extends RecyclerView.ViewHolder {

    public boolean isSelected;
    Context context;

    public DataViewHolder(@NonNull View itemView) {
        super(itemView);
        isSelected = false;
        this.context = itemView.getContext();
    }

    public abstract void setup(int id, OnTouchEvents onTouchEvents);

    public abstract void select();

    public abstract void deselect();

    public abstract void changeViewHolderColorToAppTheme();

    public interface OnTouchEvents {
        void onSingleTapConfirmed();
        void onDoubleTap();
    }
}
