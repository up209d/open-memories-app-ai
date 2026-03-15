package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.util.AbstractRelativeLayoutGroup;
import com.sony.imaging.app.util.IVisibilityChanged;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: classes.dex */
public abstract class ActiveImage extends ImageView implements AbstractRelativeLayoutGroup.IVisibilityChange {
    private static final String[] NOTIFIER_TAGS = {CameraNotificationManager.SCENE_MODE, CameraNotificationManager.REC_MODE_CHANGED};
    protected boolean mFnMode;
    private boolean mForceInvalidAlpha;
    private NotificationListener mListener;
    protected final CameraNotificationManager mNotifier;
    protected IVisibilityChanged mNotifyTarget;

    protected abstract void refresh();

    public ActiveImage(Context context) {
        this(context, null);
    }

    public ActiveImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFnMode = false;
        this.mNotifier = CameraNotificationManager.getInstance();
        this.mListener = getNotificationListener();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ImageView, android.view.View
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
    @Override // android.widget.ImageView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mListener != null) {
            this.mNotifier.removeNotificationListener(this.mListener);
        }
        this.mNotifyTarget = null;
    }

    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new ActiveImageListener();
        }
        return this.mListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class ActiveImageListener implements NotificationListener {
        /* JADX INFO: Access modifiers changed from: protected */
        public ActiveImageListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            if (CameraNotificationManager.SCENE_MODE.equals(tag)) {
                ActiveImage.this.setVisibility((ActiveImage.this.isVisible() || ActiveImage.this.mFnMode) ? 0 : 4);
            } else if (CameraNotificationManager.REC_MODE_CHANGED.equals(tag)) {
                ActiveImage.this.setVisibility((ActiveImage.this.isVisible() || ActiveImage.this.mFnMode) ? 0 : 4);
                ActiveImage.this.refresh();
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public final String[] getTags() {
            ArrayList<String> tags = new ArrayList<>();
            tags.addAll(Arrays.asList(ActiveImage.NOTIFIER_TAGS));
            if (addTags() != null) {
                tags.addAll(Arrays.asList(addTags()));
            }
            return (String[]) tags.toArray(new String[(addTags() == null ? 0 : addTags().length) + ActiveImage.NOTIFIER_TAGS.length]);
        }

        public String[] addTags() {
            return null;
        }
    }

    protected boolean isVisible() {
        return true;
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        ViewGroup.LayoutParams params = getLayoutParams();
        if (1073741824 == View.MeasureSpec.getMode(widthMeasureSpec)) {
            width = params.width;
        }
        if (1073741824 == View.MeasureSpec.getMode(heightMeasureSpec)) {
            height = params.height;
        }
        setMeasuredDimension(width, height);
    }

    @Override // com.sony.imaging.app.util.AbstractRelativeLayoutGroup.IVisibilityChange
    public void setCallback(IVisibilityChanged target) {
        this.mNotifyTarget = target;
    }

    @Override // android.widget.ImageView, android.view.View
    public void setVisibility(int visibility) {
        if (this.mFnMode) {
            super.setVisibility(0);
            return;
        }
        int current = getVisibility();
        super.setVisibility(visibility);
        if (this.mNotifyTarget != null && visibility != current) {
            this.mNotifyTarget.onVisibilityChanged();
        }
    }

    public void setFnMode(boolean fnMode) {
        this.mFnMode = fnMode;
    }

    public void setForceInvalidAlpha(boolean force) {
        this.mForceInvalidAlpha = force;
        if (force) {
            setAlpha(128);
        }
    }
}
