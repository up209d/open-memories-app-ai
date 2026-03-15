package com.sony.imaging.app.synctosmartphone.commonUtil;

import android.util.Log;

/* loaded from: classes.dex */
public class MainCursolPosUtil {
    private static final String TAG = MainCursolPosUtil.class.getSimpleName();
    private static MainCursolPosUtil mInstance = null;
    private boolean mCursol_Right;
    private boolean mCursol_Up;
    private boolean mMediaMount;
    private boolean mTransferNowBtnEnable;

    public static MainCursolPosUtil getInstance() {
        if (mInstance == null) {
            mInstance = new MainCursolPosUtil();
        }
        return mInstance;
    }

    public void setCursolRight(boolean value) {
        this.mCursol_Right = value;
        Log.v(TAG, "setCursolRight = " + value);
    }

    public void setCursolUp(boolean value) {
        this.mCursol_Up = value;
        Log.v(TAG, "setCursolUp = " + value);
    }

    public void setMediaMount(boolean value) {
        this.mMediaMount = value;
        Log.v(TAG, "setMediaMount = " + value);
    }

    public void setTransferNowButtonEnable(boolean value) {
        this.mTransferNowBtnEnable = value;
        Log.v(TAG, "setTransferNowButtonEnable = " + value);
    }

    public boolean getCursolRight() {
        Log.v(TAG, "getCursolRight = " + this.mCursol_Right);
        return this.mCursol_Right;
    }

    public boolean getCursolUp() {
        Log.v(TAG, "getCursolUp = " + this.mCursol_Up);
        return this.mCursol_Up;
    }

    public boolean getMediaMount() {
        Log.v(TAG, "getMediaMount = " + this.mMediaMount);
        return this.mMediaMount;
    }

    public boolean getTransferNowButtonEnable() {
        Log.v(TAG, "getTransferNowButtonEnable = " + this.mTransferNowBtnEnable);
        return this.mTransferNowBtnEnable;
    }
}
