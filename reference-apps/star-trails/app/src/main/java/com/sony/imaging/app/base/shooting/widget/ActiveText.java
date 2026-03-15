package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: classes.dex */
public abstract class ActiveText extends TextView {
    private static final String[] NOTIFIER_TAGS = {CameraNotificationManager.SCENE_MODE, CameraNotificationManager.REC_MODE_CHANGED};
    private NotificationListener mListener;
    protected final CameraNotificationManager mNotifier;

    protected abstract NotificationListener getNotificationListener();

    protected abstract void refresh();

    public ActiveText(Context context) {
        this(context, null);
    }

    public ActiveText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mNotifier = CameraNotificationManager.getInstance();
        this.mListener = getNotificationListener();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        boolean visible = isVisible();
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
    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mListener != null) {
            this.mNotifier.removeNotificationListener(this.mListener);
        }
    }

    /* loaded from: classes.dex */
    protected class ActiveTextListener implements NotificationListener {
        /* JADX INFO: Access modifiers changed from: protected */
        public ActiveTextListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.SCENE_MODE.equals(tag)) {
                ActiveText.this.setVisibility(ActiveText.this.isVisible() ? 0 : 4);
            } else if (CameraNotificationManager.REC_MODE_CHANGED.equals(tag)) {
                ActiveText.this.setVisibility(ActiveText.this.isVisible() ? 0 : 4);
                ActiveText.this.refresh();
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            ArrayList<String> tags = new ArrayList<>();
            tags.addAll(Arrays.asList(ActiveText.NOTIFIER_TAGS));
            if (addTags() != null) {
                tags.addAll(Arrays.asList(addTags()));
            }
            return (String[]) tags.toArray(new String[(addTags() == null ? 0 : addTags().length) + ActiveText.NOTIFIER_TAGS.length]);
        }

        public String[] addTags() {
            return null;
        }
    }

    protected boolean isVisible() {
        return true;
    }
}
