package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.base.playback.LogHelper;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class MediaStatusIcon extends ImageView {
    private static final int ICON_HEIGHT = 32;
    private static final int ICON_WIDTH = 52;
    private static final String MSG_ON_NOTIFY = " is received.";
    public static final int NOCARD = 0;
    public static final int NORMAL = 1;
    private static final String TAG = "MediaStatusIcon";
    private MediaMountEventListener mMediaListener;
    private MediaNotificationManager mMediaNotifier;
    Drawable mMemorycard;
    Drawable mNocard;

    public MediaStatusIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.MediaStatusIcon);
        this.mNocard = attr.getDrawable(0);
        this.mMemorycard = attr.getDrawable(1);
        attr.recycle();
        this.mMediaNotifier = MediaNotificationManager.getInstance();
        this.mMediaListener = new MediaMountEventListener();
    }

    public void setValue(int status) {
        boolean isDisplay = false;
        if (status == 0) {
            if (this.mNocard != null) {
                setScaleType(ImageView.ScaleType.FIT_CENTER);
                setImageDrawable(this.mNocard);
                isDisplay = true;
            }
        } else if (status == 1 && this.mMemorycard != null) {
            this.mMemorycard.setBounds(0, 0, ICON_WIDTH, 32);
            setScaleType(ImageView.ScaleType.FIT_START);
            setImageDrawable(this.mMemorycard);
            isDisplay = true;
        }
        setVisibility(isDisplay ? 0 : 4);
    }

    /* loaded from: classes.dex */
    class MediaMountEventListener implements NotificationListener {
        MediaMountEventListener() {
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public String[] getTags() {
            String[] tags = {MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE, MediaNotificationManager.TAG_MEDIA_REMAINING_CHANGE};
            return tags;
        }

        @Override // com.sony.imaging.app.util.NotificationListener
        public void onNotify(String tag) {
            Log.d(MediaStatusIcon.TAG, LogHelper.getScratchBuilder(tag).append(MediaStatusIcon.MSG_ON_NOTIFY).toString());
            if (tag.equals(MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE)) {
                if (!MediaStatusIcon.this.mMediaNotifier.isNoCard()) {
                    int state = MediaStatusIcon.this.mMediaNotifier.getMediaState();
                    if (state == 2) {
                        MediaStatusIcon.this.setValue(1);
                        return;
                    }
                    return;
                }
                MediaStatusIcon.this.setValue(0);
            }
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setVisibility(4);
        this.mMediaNotifier.setNotificationListener(this.mMediaListener);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        this.mMediaNotifier.removeNotificationListener(this.mMediaListener);
        super.onDetachedFromWindow();
    }
}
