package com.mkpazon.kisireader.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.mkpazon.kisireader.R;

import java.io.InputStream;

/**
 * Created by mkpazon on 05/08/2017.
 */

public class LockAnimView extends View {
    private final AnimationDrawable mDrawable;
    private boolean mLocked;
    private OnDismissListener mListener;
    private boolean mDismissed = false;
    private Movie movie;

    public LockAnimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LockAnimView, 0, 0);
        try {
            mLocked = a.getBoolean(R.styleable.LockAnimView_locked, true);
        } finally {
            a.recycle();
        }


        mDrawable = (AnimationDrawable) ContextCompat.getDrawable(context, R.drawable.lock);
    }

    public boolean isLocked() {
        return mLocked;
    }

    public void setLocked(boolean locked) {
        if (locked != mLocked) {
            invalidate();
            requestLayout();
        }
    }

    public void loadGIFResource(Context context, int id) {
        //turn off hardware acceleration
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        InputStream is = context.getResources().openRawResource(id);
        movie = Movie.decodeStream(is);
    }

    public void dismiss() {
        if (!mDismissed && mListener != null) {
            mListener.onDismiss();
            mDismissed = true;
        }
    }

    public void setOnDismissListener(OnDismissListener listener) {
        mListener = listener;
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDrawable.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(400, 300); // set desired width and height of your
        
    }
}
