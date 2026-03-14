package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/* loaded from: classes.dex */
public abstract class LabelFixedFontFileInfo extends LabelFileInfo {
    public LabelFixedFontFileInfo(Context context) {
        super(context);
        setTypeface(Typeface.UNIVERS);
    }

    public LabelFixedFontFileInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.UNIVERS);
    }
}
