package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import com.sony.imaging.app.base.R;

/* loaded from: classes.dex */
public class RotationalSubLcdTextView extends SubLcdTextView implements Rotational {
    private static final String TAG = RotationalSubLcdTextView.class.getSimpleName();
    private boolean isAttached;
    private int mDisplayTime;
    private RotationFinishListener mListener;
    protected Runnable mRunnable;
    protected boolean started;
    private boolean waitingToStart;

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class TransitionRunnable implements Runnable {
        protected TransitionRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            RotationalSubLcdTextView.this.mRunnable = null;
            Log.i(RotationalSubLcdTextView.TAG, "INVISIBLE: " + RotationalSubLcdTextView.this.mText);
            RotationalSubLcdTextView.this.stop();
        }
    }

    protected Runnable getTransitionRunnable() {
        return new TransitionRunnable();
    }

    public RotationalSubLcdTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mDisplayTime = 0;
        this.mListener = null;
        this.mRunnable = null;
        this.started = false;
        this.isAttached = false;
        this.waitingToStart = false;
        initAttribute(context, attrs);
    }

    public RotationalSubLcdTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDisplayTime = 0;
        this.mListener = null;
        this.mRunnable = null;
        this.started = false;
        this.isAttached = false;
        this.waitingToStart = false;
        initAttribute(context, attrs);
    }

    public RotationalSubLcdTextView(Context context) {
        super(context);
        this.mDisplayTime = 0;
        this.mListener = null;
        this.mRunnable = null;
        this.started = false;
        this.isAttached = false;
        this.waitingToStart = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdTextView
    public void initAttribute(Context context, AttributeSet attrs) {
        super.initAttribute(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RotationalSubLcdTextView);
        String rotation = a.getString(0);
        this.mDisplayTime = rotation != null ? Integer.parseInt(rotation) : 0;
        a.recycle();
    }

    @Override // com.sony.imaging.app.base.common.widget.Rotational
    public void init() {
        setVisibility(4);
    }

    @Override // com.sony.imaging.app.base.common.widget.Rotational
    public void start() {
        if (this.isAttached) {
            startProcess();
        } else {
            this.waitingToStart = true;
        }
    }

    private void startProcess() {
        this.waitingToStart = false;
        Log.i(TAG, "startProcess: " + this.mText);
        if (isValidValue()) {
            if (this.mRunnable == null) {
                this.mText = makeText();
                setText(this.mText);
                Log.i(TAG, "VISIBLE: " + this.mText);
                setVisibility(0);
                if (this.mDisplayTime > 0) {
                    this.mRunnable = getTransitionRunnable();
                    getHandler().postDelayed(this.mRunnable, this.mDisplayTime);
                }
            }
            this.started = true;
            return;
        }
        stop();
    }

    @Override // com.sony.imaging.app.base.common.widget.Rotational
    public void stop() {
        if (this.mRunnable != null) {
            removeCallbacks(this.mRunnable);
            this.mRunnable = null;
        }
        Log.i(TAG, "INVISIBLE: " + this.mText);
        this.started = false;
        setVisibility(4);
        callRotationFinishNotify();
    }

    @Override // com.sony.imaging.app.base.common.widget.Rotational
    public void setRotationFinishListener(RotationFinishListener listener) {
        this.mListener = listener;
    }

    @Override // com.sony.imaging.app.base.common.widget.Rotational
    public void removeRotationFinishListener() {
        this.mListener = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void callRotationFinishNotify() {
        if (this.mListener != null) {
            this.mListener.onNotify(this);
        }
    }

    @Override // com.sony.imaging.app.base.common.widget.Rotational
    public boolean isValidValue() {
        return true;
    }

    protected String makeText() {
        return this.mText;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.isAttached = true;
        if (this.waitingToStart) {
            startProcess();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.isAttached = false;
    }
}
