package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public abstract class IconFileInfo extends ImageView implements NotificationListener {
    private static final String ATTR_NAME = "scaleType";
    private static final String ATTR_NAMESPACE = "http://schemas.android.com/apk/res/android";
    protected static final int INT_NONE = -1;
    protected static final long LONG_NONE = -1;
    protected final ContentsManager mMgr;
    protected final boolean mRequestNotification;

    public IconFileInfo(Context context) {
        super(context);
        this.mRequestNotification = true;
        this.mMgr = ContentsManager.getInstance();
    }

    public IconFileInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.IconFileInfo);
        this.mRequestNotification = attr.getBoolean(0, true);
        attr.recycle();
        this.mMgr = ContentsManager.getInstance();
        String scaleTypeAttribute = attrs.getAttributeValue(ATTR_NAMESPACE, ATTR_NAME);
        if (scaleTypeAttribute == null) {
            setScaleType(ImageView.ScaleType.CENTER);
        }
    }

    public void setContentInfo(ContentInfo info) {
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return new String[]{ContentsManager.NOTIFICATION_TAG_CURRENT_FILE};
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        setContentInfo((ContentInfo) this.mMgr.getValue(ContentsManager.NOTIFICATION_TAG_CURRENT_FILE));
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mRequestNotification) {
            ContentsManager.getInstance().setNotificationListener(this);
            setVisibility(4);
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        if (this.mRequestNotification) {
            ContentsManager.getInstance().removeNotificationListener(this);
        }
        super.onDetachedFromWindow();
    }
}
