package com.sony.imaging.app.base.shooting.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class RotationalSubLcdTextStillRemain extends RotationalSubLcdTextView {
    private static final String TAG = RotationalSubLcdTextStillRemain.class.getSimpleName();
    private final String[] TAGS_Still;
    private MediaMountEventListener mMediaListener;
    private MediaNotificationManager mMediaNotifier;

    public RotationalSubLcdTextStillRemain(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.TAGS_Still = new String[]{MediaNotificationManager.TAG_MEDIA_REMAINING_CHANGE};
        this.mMediaNotifier = MediaNotificationManager.getInstance();
        this.mMediaListener = new MediaMountEventListener();
    }

    public RotationalSubLcdTextStillRemain(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.TAGS_Still = new String[]{MediaNotificationManager.TAG_MEDIA_REMAINING_CHANGE};
        this.mMediaNotifier = MediaNotificationManager.getInstance();
        this.mMediaListener = new MediaMountEventListener();
    }

    public RotationalSubLcdTextStillRemain(Context context) {
        super(context);
        this.TAGS_Still = new String[]{MediaNotificationManager.TAG_MEDIA_REMAINING_CHANGE};
        this.mMediaNotifier = MediaNotificationManager.getInstance();
        this.mMediaListener = new MediaMountEventListener();
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.Rotational
    public boolean isValidValue() {
        return super.isValidValue();
    }

    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView
    protected String makeText() {
        String str = this.mText;
        MediaNotificationManager mediaNotifyMng = MediaNotificationManager.getInstance();
        if (mediaNotifyMng.isNoCard()) {
            return getResources().getString(R.string.lock_to_app_unlock_password);
        }
        int remain = mediaNotifyMng.getRemaining();
        if (remain >= 0) {
            return String.format("%05d", Integer.valueOf(remain));
        }
        return getResources().getString(R.string.lock_to_app_unlock_password);
    }

    /* loaded from: classes.dex */
    class MediaMountEventListener implements NotificationListener {
        MediaMountEventListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            return RotationalSubLcdTextStillRemain.this.getMediaMountEvent();
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            RotationalSubLcdTextStillRemain.this.refresh();
        }
    }

    protected String[] getMediaMountEvent() {
        return this.TAGS_Still;
    }

    @Override // com.sony.imaging.app.base.common.widget.AbstractSubLCDView
    protected void refresh() {
        if (getVisibility() == 0) {
            String text = makeText();
            setText(text);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mMediaNotifier.setNotificationListener(this.mMediaListener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.common.widget.RotationalSubLcdTextView, com.sony.imaging.app.base.common.widget.SubLcdTextView, com.sony.imaging.app.base.common.widget.AbstractSubLCDView, android.view.View
    public void onDetachedFromWindow() {
        this.mMediaNotifier.removeNotificationListener(this.mMediaListener);
        super.onDetachedFromWindow();
    }
}
