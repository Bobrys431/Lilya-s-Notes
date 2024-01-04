package com.example.lilyasnotes;

import android.view.View;

import androidx.annotation.NonNull;

public class Image implements Data
{
    public Image(byte[] blob)
    {

    }

    public static class ImageViewHolder extends DataViewHolder
    {

        public ImageViewHolder(@NonNull View itemView)
        {
            super(itemView);
        }
    }
}
