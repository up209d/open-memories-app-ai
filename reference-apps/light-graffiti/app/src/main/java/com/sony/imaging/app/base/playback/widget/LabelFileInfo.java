package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentsManager;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public abstract class LabelFileInfo extends TextView implements NotificationListener {
    protected final ContentsManager mMgr;

    public LabelFileInfo(Context context) {
        super(context);
        this.mMgr = ContentsManager.getInstance();
    }

    public LabelFileInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMgr = ContentsManager.getInstance();
    }

    public LabelFileInfo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
    @Override // android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ContentsManager.getInstance().setNotificationListener(this);
        setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindow() {
        ContentsManager.getInstance().removeNotificationListener(this);
        super.onDetachedFromWindow();
    }

    public void setContentInfo(ContentInfo info) {
    }
}
