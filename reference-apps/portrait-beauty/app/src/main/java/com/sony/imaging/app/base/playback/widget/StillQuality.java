package com.sony.imaging.app.base.playback.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.playback.contents.AVIndexContentInfo;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class StillQuality extends IconFileInfo {
    public static final long IMAGE_QUALITY_EXTRA_FINE = 5;
    public static final long IMAGE_QUALITY_FINE = 2;
    public static final long IMAGE_QUALITY_RAW = 0;
    public static final long IMAGE_QUALITY_RAW_JPEG = 6;
    public static final long IMAGE_QUALITY_STANDARD = 3;

    public StillQuality(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ImageView.ScaleType.CENTER);
    }

    @Override // com.sony.imaging.app.base.playback.widget.IconFileInfo
    public void setContentInfo(ContentInfo info) {
        long stillQuality = -1;
        if (info != null && !isPanorama(info)) {
            stillQuality = info.getLong("MkNoteImgQual");
        }
        setValue(stillQuality);
    }

    public boolean isPanorama(ContentInfo info) {
        if (info == null) {
            return false;
        }
        boolean result = AVIndexContentInfo.isPanorama(info);
        return result;
    }

    public void setValue(long stillQuality) {
        boolean isDisplay = false;
        if (6 == stillQuality) {
            setImageResource(17304005);
            isDisplay = true;
        } else if (0 == stillQuality) {
            setImageResource(R.drawable.ic_wifi_signal_0);
            isDisplay = true;
        } else if (2 == stillQuality) {
            setImageResource(R.drawable.ic_wifi_signal_2);
            isDisplay = true;
        } else if (3 == stillQuality) {
            setImageResource(R.drawable.immersive_cling_bg_circ);
            isDisplay = true;
        } else if (5 == stillQuality) {
            setImageResource(R.drawable.indicator_check_mark_dark);
            isDisplay = true;
        }
        setVisibility(isDisplay ? 0 : 4);
    }
}
