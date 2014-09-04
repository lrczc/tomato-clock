package com.example.myapp;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by shizhao.czc on 2014/9/4.
 */
public class MyPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.6f;

    private static final float ROTATION_ANGLE = 60;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();
        float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);

        } else if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page
            view.setAlpha(1);
            //view.setTranslationX(0);
//            view.setPivotX(0.5f*pageWidth);
//            view.setPivotY(0.5f*pageHeight);
//            view.setScaleX(scaleFactor);
//            view.setScaleY(scaleFactor);
            view.setPivotX(pageWidth);
            view.setPivotY(0.5f*pageHeight);
            view.setRotationY(position*ROTATION_ANGLE);
        } else if (position <= 1) { // (0,1]
            // Fade the page out.
            //view.setAlpha(1 - position);

            // Counteract the default slide transition
            //view.setTranslationX(pageWidth * -position);

            // Scale the page down (between MIN_SCALE and 1)
            view.setAlpha(1);
            //view.setTranslationX(0);
//            view.setPivotX(0.5f*pageWidth);
//            view.setPivotY(0.5f*pageHeight);
//            view.setScaleX(scaleFactor);
//            view.setScaleY(scaleFactor);
            view.setPivotX(0);
            view.setPivotY(0.5f*pageHeight);
            view.setRotationY(position*ROTATION_ANGLE);
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }
}