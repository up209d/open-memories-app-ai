package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.SubLcdManager;

/* loaded from: classes.dex */
public class SubLcdActiveTextProgress extends ShootingSubLcdActiveText {
    private static final String TAG = "SubLcdActiveTextProgress";
    protected SubLcdManager.BlinkHandle mHandle;

    public SubLcdActiveTextProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mHandle = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onAttachedToWindow() {
        this.mHandle = SubLcdManager.getInstance().blinkAll("PTN_SLOW");
        super.onAttachedToWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onDetachedFromWindow() {
        if (this.mHandle != null) {
            SubLcdManager.getInstance().stopBlink(this.mHandle);
            this.mHandle = null;
        }
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveText, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public void refresh() {
    }
}
