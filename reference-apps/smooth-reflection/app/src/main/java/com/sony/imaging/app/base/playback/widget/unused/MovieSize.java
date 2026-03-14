package com.sony.imaging.app.base.playback.widget.unused;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.base.playback.widget.IconFileInfo;

/* loaded from: classes.dex */
public class MovieSize extends IconFileInfo {
    private static final int MOVIESIZE_FULL_HD = 1080;
    private static final int MOVIESIZE_HD = 720;
    private static final int MOVIESIZE_SD = 480;

    @Deprecated
    public MovieSize(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ImageView.ScaleType.CENTER);
    }

    @Override // com.sony.imaging.app.base.playback.widget.IconFileInfo
    @Deprecated
    public void setContentInfo(ContentInfo info) {
        int recQuality = info.getInt("AVIRecQuality");
        setAVCHDValue(recQuality);
        long imgHeight = info.getLong("ImageLength");
        setMP4Value(imgHeight);
    }

    @Deprecated
    public void setAVCHDValue(int recQuality) {
        setVisibility(0 != 0 ? 0 : 4);
    }

    @Deprecated
    public void setMP4Value(long imgHeight) {
        boolean isDisplay = false;
        if (imgHeight == 1080) {
            setImageResource(R.drawable.textfield_bg_default_holo_dark);
            isDisplay = true;
        } else if (imgHeight == 720) {
            setImageResource(17304975);
            isDisplay = true;
        } else if (imgHeight == 480) {
            setImageResource(17305490);
            isDisplay = true;
        }
        setVisibility(isDisplay ? 0 : 4);
    }
}
