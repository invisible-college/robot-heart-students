package com.invisiblecollege.robotgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by randy.thedford on 11/27/15.
 */
public class CustomView extends View {

    long mNowTime = 0;
    long mLastTime = 0;
    long mElapsedTime = 0;
    boolean mRunning = true;
    CustomViewEvents mCallback;

    public CustomView(Context context) {
        super(context);
        init();
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        mLastTime = System.nanoTime() / 1000000;
    }

    interface CustomViewEvents {
        void onUpdate(long elapsedTime);
        void onDraw(Canvas c);
    }

    public void setCustomViewListener(CustomViewEvents listener) {
        mCallback = listener;
    }

    public void pause() {
        mRunning = false;
    }

    public void resume() {
        mRunning = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Calculate Time
        mNowTime = System.nanoTime() / 1000000;
        mElapsedTime = mNowTime - mLastTime;
        mLastTime = mNowTime;

        if (mCallback != null) {
            mCallback.onUpdate(mElapsedTime);
            mCallback.onDraw(canvas);
        }

        if (mRunning) {
            this.postInvalidate();
        }
    }

}
