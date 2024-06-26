package com.example.lilyasnotes.RecyclerViewAdapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lilyasnotes.Data.ViewHolders.FooterView;
import com.example.lilyasnotes.Data.ViewHolders.NoteView;
import com.example.lilyasnotes.Data.ViewHolders.ThemeView;

public class RecyclerViewMoveCallback extends ItemTouchHelper.Callback {

    TouchHelperContract touchHelperContract;

    public RecyclerViewMoveCallback(TouchHelperContract touchHelperContract) {
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
        System.out.println("getMovementFlags");
        int movementFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(movementFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        System.out.println("onMove");
        byte type = 0;

        if (viewHolder instanceof ThemeView)
            { type = AbstractRecyclerViewAdapter.VIEW_TYPE_THEME; }
        else if (viewHolder instanceof NoteView)
            { type = AbstractRecyclerViewAdapter.VIEW_TYPE_NOTE; }
        else if (viewHolder instanceof FooterView)
            { type = AbstractRecyclerViewAdapter.VIEW_TYPE_FOOTER; }

        touchHelperContract.onMoved(type, viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
    }

    public interface TouchHelperContract {
        void onMoved(byte type, int from, int to);
    }
}
