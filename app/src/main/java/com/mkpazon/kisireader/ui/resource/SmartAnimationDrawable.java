package com.mkpazon.kisireader.ui.resource;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;

/**
 * Created by mkpazon on 05/08/2017.
 *
 * Drawable that tracks the end of animation
 *
 */
public class SmartAnimationDrawable extends AnimationDrawable {

    private OnAnimationFinishListener mOnAnimationFinishListener;

    public SmartAnimationDrawable(AnimationDrawable aniDrawable) {
        /* Add each frame to our animation drawable */
        for (int i = 0; i < aniDrawable.getNumberOfFrames(); i++) {
            this.addFrame(aniDrawable.getFrame(i), aniDrawable.getDuration(i));
        }
    }

    @Override
    public void start() {
        super.start();

        Handler mAnimationHandler = new Handler();
        mAnimationHandler.postDelayed(new Runnable() {
            public void run() {
                if(mOnAnimationFinishListener!=null) {
                    mOnAnimationFinishListener.onAnimationFinish();
                }
            }
        }, getTotalDuration());
    }

    /**
     * Gets the total duration of all frames.
     *
     * @return The total duration.
     */
    private int getTotalDuration() {
        int totalDuration = 0;
        for (int i = 0; i < this.getNumberOfFrames(); i++) {
            totalDuration += this.getDuration(i);
        }
        return totalDuration;
    }

    public void setOnAnimationFinishListener(OnAnimationFinishListener listener) {
        this.mOnAnimationFinishListener = listener;
    }

    public interface OnAnimationFinishListener {
        void onAnimationFinish();
    }
}