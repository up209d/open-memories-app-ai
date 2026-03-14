package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: classes.dex */
public abstract class ActiveLayout extends RelativeLayout {
    private static final String[] NOTIFIER_TAGS = {CameraNotificationManager.SCENE_MODE, CameraNotificationManager.REC_MODE_CHANGED};
    private boolean mFnMode;
    protected NotificationListener mListener;
    protected final CameraNotificationManager mNotifier;

    protected abstract NotificationListener getNotificationListener();

    protected abstract void refresh();

    public ActiveLayout(Context context) {
        this(context, null);
    }

    public ActiveLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFnMode = false;
        this.mNotifier = CameraNotificationManager.getInstance();
        this.mListener = getNotificationListener();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        boolean visible = isVisible() || this.mFnMode;
        if (this.mListener != null) {
            this.mNotifier.setNotificationListener(this.mListener);
        }
        if (visible) {
            setVisibility(0);
            refresh();
        } else {
            setVisibility(4);
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

    /* loaded from: classes.dex */
    protected class ActiveLayoutListener implements NotificationListener {
        /* JADX INFO: Access modifiers changed from: protected */
        public ActiveLayoutListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.SCENE_MODE.equals(tag)) {
                ActiveLayout.this.setVisibility((ActiveLayout.this.isVisible() || ActiveLayout.this.mFnMode) ? 0 : 4);
            } else if (CameraNotificationManager.REC_MODE_CHANGED.equals(tag)) {
                ActiveLayout.this.setVisibility((ActiveLayout.this.isVisible() || ActiveLayout.this.mFnMode) ? 0 : 4);
                ActiveLayout.this.refresh();
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            ArrayList<String> tags = new ArrayList<>();
            tags.addAll(Arrays.asList(ActiveLayout.NOTIFIER_TAGS));
            if (addTags() != null) {
                tags.addAll(Arrays.asList(addTags()));
            }
            return (String[]) tags.toArray(new String[(addTags() == null ? 0 : addTags().length) + ActiveLayout.NOTIFIER_TAGS.length]);
        }

        public String[] addTags() {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isVisible() {
        return true;
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        if (this.mFnMode) {
            visibility = 0;
        }
        super.setVisibility(visibility);
    }

    public void setAlpha(int alpha) {
        int ct = getChildCount();
        for (int i = 0; i < ct; i++) {
            View v = getChildAt(i);
            if (v instanceof ImageView) {
                ((ImageView) v).setAlpha(alpha);
            }
        }
    }

    public void setFnMode(boolean fnMode) {
        this.mFnMode = true;
    }
}
