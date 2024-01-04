package com.example.lilyasnotes;

import android.widget.ImageView;

public class Decoration {

    final private ImageView imageView;
    final private int imageType;

    public Decoration(ImageView imageView, int imageType) {
        this.imageView = imageView;
        this.imageType = imageType;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public int getImageType() {
        return imageType;
    }
}
