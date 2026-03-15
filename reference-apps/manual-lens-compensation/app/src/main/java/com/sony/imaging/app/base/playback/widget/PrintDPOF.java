package com.sony.imaging.app.base.playback.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class PrintDPOF extends IconFileInfo {
    private static final int DPOF_STATUS_ON = 1;

    public PrintDPOF(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (getDrawable() == null) {
            setImageResource(R.drawable.clock_hand_hour);
        }
        setScaleType(ImageView.ScaleType.CENTER);
    }

    @Override // com.sony.imaging.app.base.playback.widget.IconFileInfo
    public void setContentInfo(ContentInfo info) {
        int statusDPOF = -1;
        if (info != null) {
            statusDPOF = info.getInt("DPOFDpofPrintStatus");
        }
        setValue(statusDPOF);
    }

    public void setValue(int statusDPOF) {
        boolean isDisplay = false;
        if (statusDPOF == 1) {
            isDisplay = true;
        }
        setVisibility(isDisplay ? 0 : 4);
    }
}
