
This time we are going to code the CustomView class.  Lets first start by extending the CustomView by the View class.

<pre><code>
extends View
</code></pre>

You will notice an error in the code.  At this point you can right click on the error and click "generate".  Then click Constructor and at least the first 2 constructors in the list.

Generated code 

<pre><code>
    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
</code></pre>

Next create an init() method inside this class call in from each of the CustomView constructors.

Next we will add in the members varibles:

<pre><code>
    //Timing Vars
    long mNowTime = 0;
    long mLastTime = 0;
    long mElapsedTime = 0;

    boolean mRunning = true;

</code></pre>


Next in the init method type:
<pre><code>
	mLastTime = System.nanoTime() / 1000000;
</code></pre>


Now we need to create an interface called CustomViewEvents:
<pre><code>
    interface CustomViewEvents {
        void onUpdate(long elapsedTime);
        void onDraw(Canvas c);
    }
</code></pre>

Then we create a member variable of type CustomViewEvents:

<pre><code>
	CustomViewEvents mCallback;
</code></pre>


Next we will create a setting for the listener so we can call back to the code that needs a callback
    
<pre><code>
    public void setCustomViewListener(CustomViewEvents listener){
        mCallback = listener;
    }
</code></pre>

Now lets add in the pause and resume methods:

<pre><code>
    public void pause(){
        mRunning = false;
    }

    public void resume(){
        mRunning = true;
    }


</code></pre>

and last but not least lets override the onDraw method of the class

<pre><code>
    @Override
    protected void onDraw(Canvas canvas) {

        //Calculate time
        mNowTime = System.nanoTime() / 1000000;
        mElapsedTime = mNowTime - mLastTime;
        mLastTime = mNowTime;


        if (mCallback != null){
            mCallback.onUpdate(mElapsedTime);
            mCallback.onDraw(canvas);
        }


        if (mRunning) {
            this.postInvalidate();
        }
    }

</code></pre>


I think to wrap up the class we should review the AnimatedSprite class and maybe talk about any parts of the app that you have questions about.


