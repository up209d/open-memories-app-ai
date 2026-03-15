package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.imaging.app.util.NotificationManager;

/* loaded from: classes.dex */
public class MediaSubLCDIcon extends SubLcdActiveIcon {
    private static final String TAG = "MediaSubLcdIcon";
    private final String[] TAGS_Media;

    public MediaSubLCDIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAGS_Media = new String[]{MediaNotificationManager.TAG_MEDIA_REMAINING_CHANGE, MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
    }

    public MediaSubLCDIcon(Context context) {
        super(context);
        this.TAGS_Media = new String[]{MediaNotificationManager.TAG_MEDIA_REMAINING_CHANGE, MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
    }

    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveIcon
    protected NotificationManager getNotificationManager() {
        return MediaNotificationManager.getInstance();
    }

    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveIcon
    protected NotificationListener getNotificationListener() {
        if (this.mListener == null) {
            this.mListener = new MediaSubLCDIconListener();
        }
        return this.mListener;
    }

    /* loaded from: classes.dex */
    protected class MediaSubLCDIconListener implements NotificationListener {
        protected MediaSubLCDIconListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            boolean visible = MediaSubLCDIcon.this.isVisible();
            MediaSubLCDIcon.this.setOwnVisible(visible);
            if (visible) {
                MediaSubLCDIcon.this.refresh();
            }
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public final String[] getTags() {
            return MediaSubLCDIcon.this.TAGS_Media;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveIcon, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public void refresh() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.SubLcdActiveIcon, com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    public boolean isVisible() {
        MediaNotificationManager mMedia = MediaNotificationManager.getInstance();
        if (!mMedia.isError() && !mMedia.isNoCard()) {
            return false;
        }
        return true;
    }
}
