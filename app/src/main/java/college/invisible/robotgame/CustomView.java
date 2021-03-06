package college.invisible.robotgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by randy.thedford on 11/27/15.
 */
public class CustomView extends View{

    //Timing Vars
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

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mLastTime = System.nanoTime() / 1000000;
    }

    public void setCustomViewListener(CustomViewEvents listener){
        mCallback = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //Calculate time
        mNowTime = System.nanoTime() / 1000000;
        mElapsedTime = mNowTime - mLastTime;
        mLastTime = mNowTime;


        mCallback.onUpdate(mElapsedTime);
        mCallback.onDraw(canvas);

        if (mRunning) {
            this.postInvalidate();
        }
    }

    public void pause(){
        mRunning = false;
    }

    public void resume(){
        mRunning = true;
    }

    interface CustomViewEvents {
        void onUpdate(long elapsedTime);
        void onDraw(Canvas c);
    }
}
