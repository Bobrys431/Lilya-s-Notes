package com.example.lilyasnotes.RecyclerViews;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Activities.ThemeActivity;
import com.example.lilyasnotes.Data.ViewHolders.AbstractViewHolder;
import com.example.lilyasnotes.Data.ViewHolders.NoteViewHolder;
import com.example.lilyasnotes.Data.ViewHolders.ThemeViewHolder;

public class RecyclerViewMoveCallback extends ItemTouchHelper.Callback {

    private final RecyclerViewTouchHelperContract touchHelperContract;

    public RecyclerViewMoveCallback(RecyclerViewTouchHelperContract touchHelperContract) {
        this.touchHelperContract = touchHelperContract;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlag, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        if (viewHolder instanceof AbstractViewHolder) {
            int type = ThemeActivity.NO_TYPE;
            if (viewHolder instanceof ThemeViewHolder) {
                type = ThemeActivity.THEME_TYPE;
            } else if (viewHolder instanceof NoteViewHolder) {
                type = ThemeActivity.NOTE_TYPE;
            }

            touchHelperContract.onMoved(type, viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }

        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    public interface RecyclerViewTouchHelperContract {
        void onMoved(int type, int from, int to);
    }
}
