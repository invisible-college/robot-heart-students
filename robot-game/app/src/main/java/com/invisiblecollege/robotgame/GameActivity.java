package com.invisiblecollege.robotgame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by randy.thedford on 11/27/15.
 */
public class GameActivity extends AppCompatActivity implements CustomView.CustomViewEvents, View.OnTouchListener {

    CustomView mGameView;

    RectF mScreenRect = new RectF();

    int mLoops = 0;
    int mScore = 0;

    Paint mPaint;
    Paint mFloorPaint;

    AnimatedSprite mRobot;

    long mTimer = 0;
    long mMaxTimer = 60000;

    SoundPool mSoundPool;

    int mHitSound;
    int mMissSound;
    int mGameOverSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mGameView = (CustomView) findViewById(R.id.game_view);
        mGameView.setCustomViewListener(this);
        mGameView.setOnTouchListener(this);

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(50);

        mFloorPaint = new Paint();
        Bitmap floorBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.floor);
        BitmapShader shader = new BitmapShader(floorBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mFloorPaint.setShader(shader);

        ArrayList robotBitmaps = new ArrayList<>();

        robotBitmaps.add( BitmapFactory.decodeResource(getResources(), R.mipmap.robot_move_01) );
        robotBitmaps.add( BitmapFactory.decodeResource(getResources(), R.mipmap.robot_move_02) );
        robotBitmaps.add( BitmapFactory.decodeResource(getResources(), R.mipmap.robot_move_03) );
        robotBitmaps.add( BitmapFactory.decodeResource(getResources(), R.mipmap.robot_move_04) );
        robotBitmaps.add( BitmapFactory.decodeResource(getResources(), R.mipmap.robot_move_05) );
        robotBitmaps.add( BitmapFactory.decodeResource(getResources(), R.mipmap.robot_move_06) );
        robotBitmaps.add( BitmapFactory.decodeResource(getResources(), R.mipmap.robot_move_07) );
        robotBitmaps.add( BitmapFactory.decodeResource(getResources(), R.mipmap.robot_move_08) );

        mRobot = new AnimatedSprite(robotBitmaps);

        setupSound();
    }

    public void setupSound() {
    //check 5.0
        if (Build.VERSION.SDK_INT >= 21) {
            mSoundPool = new SoundPool.Builder().setMaxStreams(5).build();
        } else {
            mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }

        mHitSound = mSoundPool.load(this, R.raw.click1, 1);
        mMissSound = mSoundPool.load(this, R.raw.click2, 1);
        mGameOverSound = mSoundPool.load(this, R.raw.gameover, 1);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGameView != null) {
            mGameView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGameView != null) {
            mGameView.pause();
        }
    }

    @Override
    public void onUpdate(long elapsedTime) {
        mScreenRect.set(0, 0, mGameView.getWidth(), mGameView.getHeight());
        mLoops++;

        mTimer += elapsedTime;

        if (mTimer > mMaxTimer){
            gameOver();
        }

        mRobot.update(elapsedTime);

        //Check if robot escaped
        if (!RectF.intersects(mScreenRect, mRobot.getRect())){
            //change to random positions
            mRobot.randomized(mScreenRect);

            //penalty
            mScore -= 50;
            if (mScore < 0){
                mScore = 0;
            }
        }

        if (mRobot.isDead()){
            if (mScreenRect.width() > 0 && mScreenRect.height() > 0){
                mRobot.randomized(mScreenRect);
            }
        }
    }

    @Override
    public void onDraw(Canvas c) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }


}
