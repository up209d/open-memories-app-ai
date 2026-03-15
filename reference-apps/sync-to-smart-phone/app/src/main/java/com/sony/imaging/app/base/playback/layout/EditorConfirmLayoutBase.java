package com.sony.imaging.app.base.playback.layout;

import android.graphics.drawable.Drawable;

/* loaded from: classes.dex */
public abstract class EditorConfirmLayoutBase extends PlayLayoutBase {
    protected OnConfirmListener mOnConfirmListener;

    /* loaded from: classes.dex */
    public interface OnConfirmListener {
        void onCancel();

        void onOk();
    }

    public void setOnConfirmListener(OnConfirmListener listener) {
        this.mOnConfirmListener = listener;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    public int getResumeKeyBeepPattern() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        OnConfirmListener listener = this.mOnConfirmListener;
        if (listener != null) {
            listener.onCancel();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        OnConfirmListener listener = this.mOnConfirmListener;
        if (listener != null) {
            listener.onCancel();
            return 1;
        }
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout
    public Drawable onGetBackground() {
        return BACKGROUND_FOLLOW;
    }
}
