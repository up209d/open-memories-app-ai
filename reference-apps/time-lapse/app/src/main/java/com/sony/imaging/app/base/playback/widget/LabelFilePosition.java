package com.sony.imaging.app.base.playback.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.AudioVolumeController;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class LabelFilePosition extends LabelFixedFontFileInfo {
    private final int MAX_CONTENTS;
    private int mCount;
    private int mIndex;

    public LabelFilePosition(Context context) {
        super(context);
        this.mIndex = AudioVolumeController.INVALID_VALUE;
        this.mCount = AudioVolumeController.INVALID_VALUE;
        this.MAX_CONTENTS = 99999;
    }

    public LabelFilePosition(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIndex = AudioVolumeController.INVALID_VALUE;
        this.mCount = AudioVolumeController.INVALID_VALUE;
        this.MAX_CONTENTS = 99999;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.widget.LabelFileInfo, android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        this.mIndex = AudioVolumeController.INVALID_VALUE;
        this.mCount = AudioVolumeController.INVALID_VALUE;
        super.onAttachedToWindow();
    }

    public void setValue(int index, int count) {
        if (index != this.mIndex || count != this.mCount) {
            if (count > 0 && count <= 99999) {
                setText(String.format(getResources().getString(R.string.ringtone_unknown), Integer.valueOf(index + 1), Integer.valueOf(count)));
                setVisibility(0);
            } else {
                setVisibility(4);
            }
        }
        this.mIndex = index;
        this.mCount = count;
    }

    @Override // com.sony.imaging.app.base.playback.widget.LabelFileInfo
    public void setContentInfo(ContentInfo info) {
        int count = AudioVolumeController.INVALID_VALUE;
        boolean isMgrReady = this.mMgr.isInitialized();
        int index = isMgrReady ? this.mMgr.getContentsTotalPosition() : Integer.MIN_VALUE;
        if (isMgrReady) {
            count = this.mMgr.getContentsTotalCount();
        }
        setValue(index, count);
    }
}
