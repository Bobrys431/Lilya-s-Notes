package com.example.lilyasnotes.Data.ViewHolders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Activities.AbstractActivity;

public abstract class AbstractViewHolder extends RecyclerView.ViewHolder {

    final public static float ALPHA_NOT_SELECTED = 0.5f;
    final public static float ALPHA_TO_SELECT = 0.75f;
    final public static float ALPHA_SELECTED = 0.9f;

    public float alphaState;
    public boolean isSelected;
    protected AbstractActivity activity;

    public AbstractViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void setup(int id, AbstractActivity activity, OnTouchEvents onTouchEvents);

    public abstract void select();
    public abstract void deselect();

    public abstract void animateAlphaState();

    public abstract void visualizeLikeSelected();
    public abstract void visualizeLikeDeselected();


    public abstract void changeColorByAppTheme();

    public interface OnTouchEvents {
        void onSingleTapConfirmed();
        void onDoubleTap();
    }
}
