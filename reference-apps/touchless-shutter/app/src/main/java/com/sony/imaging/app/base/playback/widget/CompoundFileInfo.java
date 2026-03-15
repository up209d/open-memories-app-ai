package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public abstract class CompoundFileInfo extends RelativeLayout implements NotificationListener {
    protected final ContentsManager mMgr;

    public CompoundFileInfo(Context context) {
        super(context);
        this.mMgr = ContentsManager.getInstance();
    }

    public CompoundFileInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMgr = ContentsManager.getInstance();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return new String[]{ContentsManager.NOTIFICATION_TAG_CURRENT_FILE};
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        setContentInfo((ContentInfo) this.mMgr.getValue(ContentsManager.NOTIFICATION_TAG_CURRENT_FILE));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ContentsManager.getInstance().setNotificationListener(this);
        setVisibility(4);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        ContentsManager.getInstance().removeNotificationListener(this);
        super.onDetachedFromWindow();
    }

    public void setContentInfo(ContentInfo info) {
    }
}
