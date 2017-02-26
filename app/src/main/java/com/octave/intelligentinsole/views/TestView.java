package com.octave.intelligentinsole.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import java.util.Random;

/**
 * Created by Paosin Von Scarlet on 2017/2/26.
 */

public class TestView extends SurfaceView {
    private LayerDrawable mDrawables;

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDrawables = (LayerDrawable) getBackground();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mDrawables.getIntrinsicWidth(), mDrawables.getIntrinsicHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Drawable drawable = findDrawable(x, y);
            if (drawable != null)
                drawable.setColorFilter(randomColor(), PorterDuff.Mode.SRC_IN);
        }

        return super.onTouchEvent(event);
    }

    private int randomColor() {
        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        return color;
    }

    private Drawable findDrawable(float x, float y) {
        final int numberOfLayers = mDrawables.getNumberOfLayers();
        Drawable drawable = null;
        Bitmap bitmap = null;
        for (int i = numberOfLayers - 1; i >= 0; i--) {
            drawable = mDrawables.getDrawable(i);
            bitmap = ((BitmapDrawable) drawable).getBitmap();
            try {
                int pixel = bitmap.getPixel((int) x, (int) y);
                if (pixel == Color.TRANSPARENT) {
                    continue;
                }
            } catch (Exception e) {
                continue;
            }
            return drawable;
        }
        return null;
    }

    public void initColor(int[] num){
        for (int i = 0; i < num.length; i++) {
            Drawable drawable = mDrawables.getDrawable(num[i]);
            if (drawable != null)
                drawable.setColorFilter(randomColor(), PorterDuff.Mode.SRC_IN);
        }

    }
}
