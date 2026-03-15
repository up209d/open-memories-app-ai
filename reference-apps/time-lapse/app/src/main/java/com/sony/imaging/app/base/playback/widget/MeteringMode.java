package com.sony.imaging.app.base.playback.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.playback.contents.ContentInfo;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class MeteringMode extends IconFileInfo {
    private static final int PF_VERSION_SUPPORTING_TAG_METERING_MODE_ENTIRE_SCREEN = 17;
    private static final int VAL_METERING_MODE_CENTER_WEIGHTED = 2;
    private static final int VAL_METERING_MODE_ENTIRE_SCREEN = 1;
    private static final int VAL_METERING_MODE_PATTERN = 5;
    private static final int VAL_METERING_MODE_SPOT = 3;
    private static final int VAL_MK_METERING_MODE_CENTER_WEIGHTED = 512;
    private static final int VAL_MK_METERING_MODE_ENTIRE_SCREEN = 1024;
    private static final int VAL_MK_METERING_MODE_HIGHLIGHT = 1280;
    private static final int VAL_MK_METERING_MODE_PATTERN = 256;
    private static final int VAL_MK_METERING_MODE_SPOT_LARGE = 770;
    private static final int VAL_MK_METERING_MODE_SPOT_NONE = 768;
    private static final int VAL_MK_METERING_MODE_SPOT_STD = 769;

    public MeteringMode(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ImageView.ScaleType.CENTER);
    }

    @Override // com.sony.imaging.app.base.playback.widget.IconFileInfo
    public void setContentInfo(ContentInfo info) {
        long meteringMode = -2147483648L;
        boolean mkNote = false;
        if (info != null) {
            meteringMode = info.getInt("MkNoteSettingOfMeteringMode");
            mkNote = true;
            if (meteringMode == -2147483648L) {
                meteringMode = info.getInt("MeteringMode");
                mkNote = false;
            }
        }
        setValue(meteringMode, mkNote);
    }

    public void setValue(long meteringMode, boolean mknote) {
        boolean isDisplay = false;
        if (mknote) {
            if (meteringMode == 512) {
                setImageResource(17305389);
                isDisplay = true;
            } else if (meteringMode == 256) {
                setImageResource(17305386);
                isDisplay = true;
            } else if (meteringMode == 768) {
                setImageResource(17305391);
                isDisplay = true;
            } else if (meteringMode == 770) {
                setImageResource(R.drawable.spinner_disabled_holo_dark_am);
                isDisplay = true;
            } else if (meteringMode == 769) {
                setImageResource(R.drawable.stat_notify_email_generic);
                isDisplay = true;
            } else if (meteringMode == 1280) {
                setImageResource(R.drawable.spinner_disabled_holo_dark);
                isDisplay = true;
            } else if (meteringMode == 1024) {
                setImageResource(R.drawable.pointer_spot_touch);
                isDisplay = true;
            }
        } else if (meteringMode == 2) {
            setImageResource(17305389);
            isDisplay = true;
        } else if (meteringMode == 3) {
            setImageResource(17305391);
            isDisplay = true;
        } else if (meteringMode == 5) {
            setImageResource(17305386);
            isDisplay = true;
        } else if (meteringMode == 1 && 17 <= Environment.getVersionPfAPI()) {
            setImageResource(R.drawable.pointer_spot_touch);
            isDisplay = true;
        }
        setVisibility(isDisplay ? 0 : 4);
    }
}
