package com.sony.imaging.app.graduatedfilter.common;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

/* loaded from: classes.dex */
public class GFTextHighlighter extends Handler {
    private static final int HIGHLIGHT_DURATION_DEFAULT = 1200;
    private static final int STOP_HIGHLIGHT = 0;
    private int mHighlightDuration = HIGHLIGHT_DURATION_DEFAULT;
    private TextView mText;
    private int mTextColor;
    private int mTextColorHighlight;

    public GFTextHighlighter(TextView text, int textColor, int textColorHighlight) {
        this.mTextColor = textColor;
        this.mTextColorHighlight = textColorHighlight;
        this.mText = text;
    }

    public void setHighlightDuration(int durationMillis) {
        this.mHighlightDuration = durationMillis;
    }

    public void startHighlight() {
        if (hasMessages(0)) {
            removeMessages(0);
        }
        this.mText.setTextColor(this.mTextColorHighlight);
        sendEmptyMessageDelayed(0, this.mHighlightDuration);
    }

    public void stopHighlight() {
        removeMessages(0);
        this.mText.setTextColor(this.mTextColor);
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        this.mText.setTextColor(this.mTextColor);
    }
}
