package com.sony.imaging.app.base.playback.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class Protect extends IconFileInfo {
    public Protect(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (getDrawable() == null) {
            setImageResource(R.drawable.pointer_grabbing_icon);
        }
        setScaleType(ImageView.ScaleType.CENTER);
    }

    @Override // com.sony.imaging.app.base.playback.widget.IconFileInfo
    public void setContentInfo(ContentInfo info) {
        int statusProtect = -1;
        if (info != null) {
            statusProtect = info.getInt("FILE_SYSTEMProtectInfo");
        }
        setValue(statusProtect);
    }

    public void setValue(int statusProtect) {
        boolean isDisplay = false;
        if (statusProtect == 1) {
            isDisplay = true;
        }
        setVisibility(isDisplay ? 0 : 4);
    }
}
