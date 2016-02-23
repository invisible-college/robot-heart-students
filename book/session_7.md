The first thing we must do is Look at the activity_game.xml and look at the layout.  Noticed the custom view/class that is in the layout.  Opening that class you can see 3 grayed out methods: pause, resume, and setCustomViewListener.  The setCustomViewListener is important because it is an interface.  It allows events in CustomView to sent to another place in the app if it so requests by implementing the interface and then setting it as the callback for the custom view.

Our GameActivity needs to implement the interface and while we are at it we will implement another interface for touch interactions:

<pre><code>
    implements CustomView.CustomViewEvents, View.OnTouchListener
    </code></pre>

Now you will notice there is an error with the code.  Right click on the error text and click generate implemented methods.  This should show a list of methods that you will need to accept and have the editor auto populate the methods.

Next here are the member variables that we will be using:

<pre><code>
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
    </code></pre>

Next in the onCreate method we will be setting up the custom game view

    <pre><code>
    mGameView = (CustomView)findViewById(R.id.game_view);

    mGameView.setCustomViewListener(this);

    mGameView.setOnTouchListener(this);
    </code></pre>

Now we will create a Paint object which is used for various drawing routines like text and colors.

<pre><code>
    mPaint = new Paint();
    mPaint.setColor(Color.BLACK);
    mPaint.setTextSize(50);
    </code></pre>

Now we will add in a TypeFace from the assets folder and set it to the paint object.

<pre><code>
    Typeface typeface = Typeface.createFromAsset(getAssets(), "game_robot.ttf");

    mPaint.setTypeface(typeface);
    </code></pre>

Then we will create the floor paint object, pull in a bitmap form resources, and create a shader with that resource and stick it into the paint object.

<pre><code>
    mFloorPaint = new Paint();

    Bitmap floorBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.floor);

    BitmapShader shader = new BitmapShader(floorBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

    mFloorPaint.setShader(shader);
    </code></pre>

Next we will create a list of Bitmaps to be used in an animated sprite

<pre><code>
    ArrayList<Bitmap> robotBitmaps = new ArrayList<>();

    robotBitmaps.add( BitmapFactory.decodeResource(getResources(), R.mipmap.robot_move_01) );
    robotBitmaps.add( BitmapFactory.decodeResource(getResources(), R.mipmap.robot_move_02) );
    robotBitmaps.add( BitmapFactory.decodeResource(getResources(), R.mipmap.robot_move_03) );
    robotBitmaps.add( BitmapFactory.decodeResource(getResources(), R.mipmap.robot_move_04) );
    robotBitmaps.add( BitmapFactory.decodeResource(getResources(), R.mipmap.robot_move_05) );
    robotBitmaps.add( BitmapFactory.decodeResource(getResources(), R.mipmap.robot_move_06) );
    robotBitmaps.add( BitmapFactory.decodeResource(getResources(), R.mipmap.robot_move_07) );
    robotBitmaps.add( BitmapFactory.decodeResource(getResources(), R.mipmap.robot_move_08) );
</code></pre>

Now we create a new animated sprite 

<pre><code>
    mRobot = new AnimatedSprite(robotBitmaps);
</code></pre>

Then we call setup sound.

<pre><code>
    setupSound();
</code></pre>

This method hasn't been created yet
<pre><code>

    public void setupSound(){
        //Check if we're running on Android 5.0
        if (Build.VERSION.SDK_INT >= 21){
            mSoundPool = new SoundPool.Builder().setMaxStreams(5).build();
        }
        else{
            mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }

        mHitSound = mSoundPool.load(this, R.raw.click1, 1);
        mMissSound = mSoundPool.load(this, R.raw.click2, 1);
        mGameOverSound = mSoundPool.load(this, R.raw.gameover, 1);

    }
    </code></pre>

Next we add in the resume and pausing of the game with the android system

<pre><code>
    @Override
    protected void onPause() {
        super.onPause();
        if (mGameView != null){
            mGameView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGameView != null){
            mGameView.resume();
        }
    }
</code></pre>

Now lets add in the code to the onUpdate method

<pre><code>
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
    </code></pre>

Next we have the code for the onDraw method
<pre><code>

    //c.drawColor(Color.BLUE);

    c.drawPaint(mFloorPaint);

    mRobot.draw(c);

    c.drawText("Time: " + (60 - (mTimer / 1000)), 50, 50, mPaint);

    String scoreText = "Score: " + String.valueOf(mScore);
    float widthOfText = mPaint.measureText(scoreText);

    c.drawText(scoreText, mScreenRect.width() - 50 - widthOfText, 50, mPaint);
    </code></pre>

Now lets create the game over method

<pre><code>
    public void gameOver(){

        //play a game over sound
        mSoundPool.play(mGameOverSound, 1, 1, 1, 0, 1);

        // goto results activity;
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("score", mScore);
        startActivity(intent);
        mGameView.pause();
        finish();
    }
</code></pre>

Now lets create the userTapped method

<pre><code>
    private void userTapped(float x, float y){
        //Check to see if robot is pressed
        if (mRobot.wasTapped(x, y)){
            mScore += 10;

            //hit sound
            mSoundPool.play(mHitSound, 1, 1, 1, 0, 1);
        }
        else {

            //miss sound
            mSoundPool.play(mMissSound, 1, 1, 1, 0, 1);

        }
    }
</code></pre>

Now lets add in the code for the onTouch method

<pre><code>
    int action = event.getAction();

    switch (action &  MotionEvent.ACTION_MASK){
        case MotionEvent.ACTION_DOWN:
            float x = event.getX();
            float y = event.getY();

            //send finger data to another method

            userTapped(x, y);
            break;
        case MotionEvent.ACTION_MOVE:
            break;
        case MotionEvent.ACTION_UP:
            break;
        case MotionEvent.ACTION_POINTER_DOWN:
            break;
        case MotionEvent.ACTION_POINTER_UP:
            break;
        case MotionEvent.ACTION_CANCEL:
           break;
    }
    
    return true;
    </code></pre>
    