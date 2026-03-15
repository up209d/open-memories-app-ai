package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.NotificationManager;
import com.sony.scalar.hardware.avio.DisplayManager;

/* loaded from: classes.dex */
public class SimulatingEERelativeLayout extends RelativeLayout {
    private static final String LOG_MSG_DONOTUSEABSOLUTE = "Don't use AbsoluteLayout";
    private static final String TAG = "SimulatingEERelativeLayout";
    private NotificationListener mListener;
    private NotificationManager mNotifier;

    public SimulatingEERelativeLayout(Context context) {
        this(context, null);
    }

    public SimulatingEERelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setinit();
    }

    private void setinit() {
        this.mNotifier = DisplayModeObserver.getInstance();
        setListener();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        DisplayManager.VideoRect rect = (DisplayManager.VideoRect) this.mNotifier.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
        setLayoutSize(new Rect(rect.pxLeft, rect.pxTop, rect.pxRight, rect.pxBottom));
        if (this.mListener != null) {
            this.mNotifier.setNotificationListener(this.mListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mListener != null) {
            this.mNotifier.removeNotificationListener(this.mListener);
        }
    }

    private void setListener() {
        this.mListener = new NotificationListener() { // from class: com.sony.imaging.app.base.shooting.widget.SimulatingEERelativeLayout.1
            private String[] TAGS = {DisplayModeObserver.TAG_YUVLAYOUT_CHANGE};

            @Override // com.sony.imaging.app.util.NotificationListener
            public void onNotify(String arg0) {
                DisplayManager.VideoRect rect = (DisplayManager.VideoRect) SimulatingEERelativeLayout.this.mNotifier.getValue(DisplayModeObserver.TAG_YUVLAYOUT_CHANGE);
                SimulatingEERelativeLayout.this.setLayoutSize(new Rect(rect.pxLeft, rect.pxTop, rect.pxRight, rect.pxBottom));
            }

            @Override // com.sony.imaging.app.util.NotificationListener
            public String[] getTags() {
                return this.TAGS;
            }
        };
    }

    public void setLayoutSize(Rect rect) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getLayoutParams();
        params.width = rect.right - rect.left;
        params.height = rect.bottom - rect.top;
        ViewParent parent = getParent();
        if ((parent instanceof RelativeLayout) || (parent instanceof FrameLayout)) {
            params.leftMargin = rect.left;
            params.topMargin = rect.top;
        } else if (parent instanceof AbsoluteLayout) {
            Log.w(TAG, LOG_MSG_DONOTUSEABSOLUTE);
        }
        setLayoutParams(params);
    }
}
