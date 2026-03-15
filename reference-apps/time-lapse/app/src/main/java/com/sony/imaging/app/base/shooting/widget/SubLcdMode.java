package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.widget.SubLcdIconView;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class SubLcdMode extends RelativeLayout {
    private static final String[] NOTIFIER_TAGS;
    protected static SparseArray<MODE> sRecModeToMode = new SparseArray<>();
    protected SubLcdIconView mCurrentMode;
    protected boolean mIsActive;
    protected NotificationListener mListener;
    protected SubLcdIconView mLive;
    protected SubLcdIconView mLoop;
    protected List<MODE> mModes;
    protected SubLcdIconView mPhoto;

    /* loaded from: classes.dex */
    public enum MODE {
        UNKNOWN,
        MOVIE,
        PHOTO,
        INTERVAL,
        LOOP,
        LIVE
    }

    static {
        sRecModeToMode.put(2, MODE.MOVIE);
        sRecModeToMode.put(4, MODE.INTERVAL);
        sRecModeToMode.put(1, MODE.PHOTO);
        sRecModeToMode.put(8, MODE.LOOP);
        NOTIFIER_TAGS = new String[]{CameraNotificationManager.REC_MODE_CHANGED};
    }

    public SubLcdMode(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mModes = new ArrayList();
        this.mIsActive = false;
        this.mListener = new ModeListener();
        initAttribute(context, attrs);
        initViews(context);
    }

    public SubLcdMode(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mModes = new ArrayList();
        this.mIsActive = false;
        this.mListener = new ModeListener();
        initAttribute(context, attrs);
        initViews(context);
    }

    public SubLcdMode(Context context) {
        super(context);
        this.mModes = new ArrayList();
        this.mIsActive = false;
        this.mListener = new ModeListener();
        initViews(context);
    }

    protected void initViews(Context context) {
        this.mCurrentMode = new SubLcdIconView(context, "LID_SUBLCD_CURRENT_MODE");
        this.mCurrentMode.setLkid("LKID_SUBLCD_CURRENT_MODE_SHOOTING");
        new RelativeLayout.LayoutParams(-1, -1);
        addView(this.mCurrentMode);
        this.mPhoto = new SubLcdIconView(context, "LID_SUBLCD_DUAL");
        this.mPhoto.setLkid("LKID_SUBLCD_DUAL");
        new RelativeLayout.LayoutParams(-1, -1);
        addView(this.mPhoto);
        this.mLoop = new SubLcdIconView(context, "LID_SUBLCD_LOOP_REC");
        this.mLoop.setLkid("LKID_SUBLCD_LOOP_REC");
        new RelativeLayout.LayoutParams(-1, -1);
        addView(this.mLoop);
        this.mLive = new SubLcdIconView(context, "LID_SUBLCD_LIVE_STREAMING");
        this.mLive.setLkid("LKID_SUBLCD_LIVE_STREAMING");
        new RelativeLayout.LayoutParams(-1, -1);
        addView(this.mLive);
        disappear();
    }

    protected void initAttribute(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SubLcdMode);
        this.mIsActive = a.getBoolean(0, false);
        a.recycle();
    }

    public void setMode(List<MODE> mode) {
        if (mode == null) {
            if (!this.mModes.isEmpty()) {
                this.mModes.clear();
                updateDisplay();
                return;
            }
            return;
        }
        if (!this.mModes.equals(mode)) {
            this.mModes = mode;
            updateDisplay();
        }
    }

    public void refresh() {
        MODE mode = convFromRecMode(ExecutorCreator.getInstance().getRecordingMode());
        this.mModes.clear();
        this.mModes.add(mode);
        updateDisplay();
    }

    public void updateDisplay() {
        disappear();
        for (MODE mode : this.mModes) {
            switch (mode) {
                case MOVIE:
                    this.mCurrentMode.setVisibility(0);
                    this.mCurrentMode.setLkid("LKID_SUBLCD_CURRENT_MODE_SHOOTING");
                    break;
                case PHOTO:
                    this.mPhoto.setVisibility(0);
                    break;
                case INTERVAL:
                    this.mCurrentMode.setVisibility(0);
                    this.mCurrentMode.setLkid("LKID_SUBLCD_INTERVAL_REC");
                    break;
                case LOOP:
                    this.mCurrentMode.setVisibility(0);
                    this.mCurrentMode.setLkid("LKID_SUBLCD_CURRENT_MODE_SHOOTING");
                    this.mLoop.setVisibility(0);
                    break;
                case LIVE:
                    this.mLive.setVisibility(0);
                    break;
            }
        }
    }

    public void disappear() {
        this.mCurrentMode.setVisibility(4);
        this.mPhoto.setVisibility(4);
        this.mLoop.setVisibility(4);
        this.mLive.setVisibility(4);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mIsActive) {
            CameraNotificationManager.getInstance().setNotificationListener(this.mListener);
            refresh();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        if (this.mIsActive) {
            CameraNotificationManager.getInstance().removeNotificationListener(this.mListener);
        }
        super.onDetachedFromWindow();
    }

    /* loaded from: classes.dex */
    protected class ModeListener implements NotificationListener {
        protected ModeListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            SubLcdMode.this.refresh();
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public final String[] getTags() {
            return SubLcdMode.NOTIFIER_TAGS;
        }
    }

    public static MODE convFromRecMode(int recMode) {
        return sRecModeToMode.get(recMode, MODE.UNKNOWN);
    }
}
