package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.sony.imaging.app.base.common.ISubLcdDrawer;
import com.sony.imaging.app.base.common.SubLcdManager;

/* loaded from: classes.dex */
public class SubLcdMute extends View implements ISubLcdDrawer {
    private static final String TAG = "SubLcdMute";

    public SubLcdMute(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SubLcdMute(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SubLcdMute(Context context) {
        super(context);
    }

    @Override // com.sony.imaging.app.base.common.ISubLcdDrawer
    public String getLId() {
        return null;
    }

    @Override // com.sony.imaging.app.base.common.ISubLcdDrawer
    public SubLcdManager.Element getSubLcdElement(String blink) {
        Log.i(TAG, "dispatchDrawSubLcd");
        return null;
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        Log.i(TAG, "onAttachedToWindow");
        super.onAttachedToWindow();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        Log.i(TAG, "onDetachedFromWindow");
        super.onDetachedFromWindow();
    }
}
