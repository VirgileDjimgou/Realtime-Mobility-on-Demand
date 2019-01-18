package com.android.gudana.hify.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

public class RoundBitmap {

    public RoundBitmap(Bitmap bitmap, int i, int i1) {
    }

    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage , int With_bit , int Height) {
        //int targetWidth = 110;
        //int targetHeight = 110;
        Bitmap targetBitmap = Bitmap.createBitmap(With_bit,
                Height,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) With_bit - 1) / 2,
                ((float) Height - 1) / 2,
                (Math.min(((float) With_bit),
                        ((float) Height)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, With_bit, Height), new Paint(Paint.FILTER_BITMAP_FLAG));
        return targetBitmap;
    }
}
