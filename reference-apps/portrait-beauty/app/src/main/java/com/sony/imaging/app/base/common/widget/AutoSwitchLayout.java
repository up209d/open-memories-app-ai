package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.sony.imaging.app.base.R;

/* loaded from: classes.dex */
public class AutoSwitchLayout extends FrameLayout implements Rotational, RotationFinishListener {
    private static final String TAG = AutoSwitchLayout.class.getSimpleName();
    private String mAutoStart;
    private int mCurrentRotationIndex;
    private RotationFinishListener mListener;
    private String mLoop;
    private Runnable mRunnable;
    private boolean startState;

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class TransitionRunnable implements Runnable {
        protected TransitionRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            KeyEvent.Callback childAt = AutoSwitchLayout.this.getChildAt(AutoSwitchLayout.this.mCurrentRotationIndex);
            if (childAt instanceof Rotational) {
                Rotational rotationalChild = (Rotational) childAt;
                rotationalChild.start();
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mCurrentRotationIndex = getNextRotatinalChildIndex(-1);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
        super.onLayout(arg0, arg1, arg2, arg3, arg4);
    }

    protected Runnable getTransitionRunnable() {
        return new TransitionRunnable();
    }

    public AutoSwitchLayout(Context context) {
        super(context);
        this.mCurrentRotationIndex = 0;
        this.mListener = null;
        this.mAutoStart = "ON";
        this.mLoop = "OFF";
        this.startState = false;
        Log.i(TAG, "constructor");
    }

    public AutoSwitchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCurrentRotationIndex = 0;
        this.mListener = null;
        this.mAutoStart = "ON";
        this.mLoop = "OFF";
        this.startState = false;
        initAttribute(context, attrs);
    }

    public AutoSwitchLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mCurrentRotationIndex = 0;
        this.mListener = null;
        this.mAutoStart = "ON";
        this.mLoop = "OFF";
        this.startState = false;
        initAttribute(context, attrs);
    }

    protected void initAttribute(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoSwitchLayout);
        this.mAutoStart = a.getString(0);
        if (this.mAutoStart == null) {
            this.mAutoStart = "ON";
        }
        this.mLoop = a.getString(1);
        if (this.mLoop == null) {
            this.mLoop = "OFF";
        }
        a.recycle();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.sony.imaging.app.base.common.widget.RotationFinishListener
    public void onNotify(Rotational rotational) {
        int index = indexOfChild((View) rotational);
        this.mCurrentRotationIndex = getNextRotatinalChildIndex(index);
        if (this.mCurrentRotationIndex != -1) {
            KeyEvent.Callback childAt = getChildAt(this.mCurrentRotationIndex);
            if (childAt instanceof Rotational) {
                ((Rotational) childAt).start();
                return;
            }
            return;
        }
        if (this.mLoop.equals("ON")) {
            reset();
            KeyEvent.Callback childAt2 = getChildAt(this.mCurrentRotationIndex);
            if (childAt2 instanceof Rotational) {
                Rotational rotationalChild = (Rotational) childAt2;
                if (rotational != rotationalChild || rotationalChild.isValidValue()) {
                    rotationalChild.start();
                    return;
                }
                return;
            }
            return;
        }
        stop();
    }

    private int getNextRotatinalChildIndex(int currentIndex) {
        for (int i = currentIndex + 1; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof Rotational) {
                int ret = i;
                return ret;
            }
        }
        return -1;
    }

    @Override // com.sony.imaging.app.base.common.widget.Rotational
    public void init() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            KeyEvent.Callback childAt = getChildAt(i);
            if (childAt instanceof Rotational) {
                Rotational rotationalChild = (Rotational) childAt;
                rotationalChild.setRotationFinishListener(this);
                rotationalChild.init();
            }
        }
        this.mCurrentRotationIndex = getNextRotatinalChildIndex(-1);
    }

    protected void reset() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof AutoSwitchLayout) {
                AutoSwitchLayout autoSwitchLayoutChild = (AutoSwitchLayout) child;
                autoSwitchLayoutChild.reset();
            }
        }
        this.mCurrentRotationIndex = getNextRotatinalChildIndex(-1);
    }

    @Override // com.sony.imaging.app.base.common.widget.Rotational
    public void start() {
        Log.i(TAG, "start");
        if (this.mRunnable == null) {
            this.mRunnable = getTransitionRunnable();
        }
        KeyEvent.Callback childAt = getChildAt(this.mCurrentRotationIndex);
        if (childAt instanceof Rotational) {
            Rotational rotationalChild = (Rotational) childAt;
            rotationalChild.start();
        }
        this.startState = true;
    }

    @Override // com.sony.imaging.app.base.common.widget.Rotational
    public void stop() {
        Log.i(TAG, "stop");
        if (this.mRunnable != null) {
            getHandler().removeCallbacks(this.mRunnable);
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                KeyEvent.Callback childAt = getChildAt(i);
                if (childAt instanceof Rotational) {
                    Rotational rotationalChild = (Rotational) childAt;
                    rotationalChild.removeRotationFinishListener();
                    rotationalChild.stop();
                }
            }
            this.startState = false;
            callRotationFinishNotify();
            this.mRunnable = null;
        }
    }

    @Override // com.sony.imaging.app.base.common.widget.Rotational
    public void setRotationFinishListener(RotationFinishListener listener) {
        this.mListener = listener;
    }

    @Override // com.sony.imaging.app.base.common.widget.Rotational
    public void removeRotationFinishListener() {
        this.mListener = null;
    }

    private void callRotationFinishNotify() {
        if (this.mListener != null) {
            this.mListener.onNotify(this);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public boolean setStartPosition(int id) {
        boolean ret = false;
        if (!this.startState) {
            int childCount = getChildCount();
            int i = 0;
            while (true) {
                if (i >= childCount) {
                    break;
                }
                View childAt = getChildAt(i);
                if ((childAt instanceof Rotational) && id == childAt.getId()) {
                    ((Rotational) childAt).init();
                    ret = true;
                } else if (childAt instanceof AutoSwitchLayout) {
                    ret = ((AutoSwitchLayout) childAt).setStartPosition(id);
                }
                if (!ret) {
                    i++;
                } else {
                    this.mCurrentRotationIndex = i;
                    break;
                }
            }
        }
        return ret;
    }

    @Override // com.sony.imaging.app.base.common.widget.Rotational
    public boolean isValidValue() {
        boolean ret = false;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            KeyEvent.Callback childAt = getChildAt(i);
            if (childAt instanceof Rotational) {
                ret |= ((Rotational) childAt).isValidValue();
            }
        }
        return ret;
    }
}
