package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class MeteringMode extends IconFileInfo {
    private static final int VAL_METERING_MODE_CENTER_WEIGHTED = 2;
    private static final int VAL_METERING_MODE_PATTERN = 5;
    private static final int VAL_METERING_MODE_SPOT = 3;

    public MeteringMode(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ImageView.ScaleType.CENTER);
    }

    @Override // com.sony.imaging.app.base.playback.widget.IconFileInfo
    public void setContentInfo(ContentInfo info) {
        int meteringMode = -1;
        if (info != null) {
            meteringMode = info.getInt("MeteringMode");
        }
        setValue(meteringMode);
    }

    public void setValue(int meteringMode) {
        boolean isDisplay = false;
        switch (meteringMode) {
            case 2:
                setImageResource(17305389);
                isDisplay = true;
                break;
            case 3:
                setImageResource(17305391);
                isDisplay = true;
                break;
            case 5:
                setImageResource(17305386);
                isDisplay = true;
                break;
        }
        setVisibility(isDisplay ? 0 : 4);
    }
}
