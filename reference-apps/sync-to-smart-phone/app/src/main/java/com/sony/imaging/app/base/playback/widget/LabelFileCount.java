package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.AudioVolumeController;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class LabelFileCount extends LabelFileInfo {
    private int mCount;

    public LabelFileCount(Context context) {
        super(context);
        this.mCount = AudioVolumeController.INVALID_VALUE;
        setCompoundDrawablesWithIntrinsicBounds(17304529, 0, 0, 0);
    }

    public LabelFileCount(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCount = AudioVolumeController.INVALID_VALUE;
        setCompoundDrawablesWithIntrinsicBounds(17304529, 0, 0, 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.widget.LabelFileInfo, android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        this.mCount = AudioVolumeController.INVALID_VALUE;
        super.onAttachedToWindow();
    }

    public void setValue(int count) {
        if (count != this.mCount) {
            if (count >= 0) {
                setText(new StringBuilder().append(count).toString());
                setVisibility(0);
            } else {
                setVisibility(4);
            }
        }
        this.mCount = count;
    }

    @Override // com.sony.imaging.app.base.playback.widget.LabelFileInfo
    public void setContentInfo(ContentInfo info) {
        boolean isMgrReady = this.mMgr.isInitialized();
        int count = isMgrReady ? this.mMgr.getContentsCount() : AudioVolumeController.INVALID_VALUE;
        setValue(count);
    }
}
