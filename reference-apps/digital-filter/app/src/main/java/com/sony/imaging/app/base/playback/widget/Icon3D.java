package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class Icon3D extends IconFileInfo {
    private static final int IMG_COUNT_3D = 2;

    public Icon3D(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (getDrawable() == null) {
            setImageResource(17304852);
        }
        setScaleType(ImageView.ScaleType.CENTER);
    }

    @Override // com.sony.imaging.app.base.playback.widget.IconFileInfo
    public void setContentInfo(ContentInfo info) {
        int contentType = -1;
        if (info != null) {
            contentType = info.getInt("ContentType");
        }
        setValue(contentType);
    }

    public void setValue(int contentType) {
        boolean isDisplay = false;
        if (contentType == 16 || contentType == 11 || contentType == 12) {
            isDisplay = true;
        }
        setVisibility(isDisplay ? 0 : 4);
    }

    public void setValue(int stillType, long imgCount, int recQuality) {
        boolean isDisplay = false;
        if ((stillType == 1 && imgCount == 2) || recQuality == 9) {
            isDisplay = true;
        }
        setVisibility(isDisplay ? 0 : 4);
    }
}
