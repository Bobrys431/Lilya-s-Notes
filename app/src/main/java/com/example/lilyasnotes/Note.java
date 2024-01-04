package com.example.lilyasnotes;

import android.view.View;

import androidx.annotation.NonNull;

public class Note implements Data
{
    public Note(String text)
    {

    }

    public static class NoteViewHolder extends DataViewHolder
    {

        public NoteViewHolder(@NonNull View itemView)
        {
            super(itemView);
        }
    }
}
