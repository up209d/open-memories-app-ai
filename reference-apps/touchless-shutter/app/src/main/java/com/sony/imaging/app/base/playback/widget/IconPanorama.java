package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.playback.contents.AVIndexContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class IconPanorama extends IconFileInfo {
    private static final int PBPANORAMASIZE_16_9_HEIGHT = 1080;
    private static final int PBPANORAMASIZE_16_9_WIDTH = 1920;

    public IconPanorama(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ImageView.ScaleType.CENTER);
    }

    @Override // com.sony.imaging.app.base.playback.widget.IconFileInfo
    public void setContentInfo(ContentInfo info) {
        boolean isPanorama = false;
        if (info != null) {
            isPanorama = AVIndexContentInfo.isPanorama(info);
        }
        if (isPanorama) {
            setImageResource(17306222);
            setVisibility(0);
        } else {
            setVisibility(4);
        }
    }

    public void setValue(int contentType, long imgWidth, long imgHeight) {
        boolean isDisplay = false;
        switch (contentType) {
            case 9:
            case 12:
                isDisplay = true;
                break;
            case 11:
                if (imgWidth == 1920 && imgHeight == 1080) {
                    isDisplay = true;
                    break;
                }
                break;
        }
        if (isDisplay) {
            setImageResource(17306222);
            setVisibility(0);
        } else {
            setVisibility(4);
        }
    }
}
