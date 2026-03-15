package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.NotificationManager;

/* loaded from: classes.dex */
public abstract class SubLcdActiveText extends SubLcdTextView {
    protected NotificationListener mListener;
    protected final NotificationManager mNotifier;

    protected abstract NotificationListener getNotificationListener();

    protected abstract NotificationManager getNotificationManager();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public abstract void refresh();

    public SubLcdActiveText(Context context) {
        this(context, null);
    }

    public SubLcdActiveText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mNotifier = getNotificationManager();
        this.mListener = getNotificationListener();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public boolean isVisible() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mListener != null && this.mNotifier != null) {
            this.mNotifier.setNotificationListener(this.mListener);
        }
        boolean visible = isVisible();
        setOwnVisible(visible);
        if (visible) {
            refresh();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mListener != null && this.mNotifier != null) {
            this.mNotifier.removeNotificationListener(this.mListener);
        }
    }
}
