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

    }

    @Override
    public void onUpdate(long elapsedTime) {

    }

    @Override
    public void onDraw(Canvas c) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
