package com.sony.imaging.app.base.playback.widget.unused;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.widget.IconFileInfo;

/* loaded from: classes.dex */
public class MovieFormat extends IconFileInfo {
    @Deprecated
    public MovieFormat(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ImageView.ScaleType.CENTER);
    }

    @Override // com.sony.imaging.app.base.playback.widget.IconFileInfo
    @Deprecated
    public void setContentInfo(ContentInfo info) {
    }

    @Deprecated
    public void setValue(int contentType) {
        setVisibility(0 != 0 ? 0 : 4);
    }
}
