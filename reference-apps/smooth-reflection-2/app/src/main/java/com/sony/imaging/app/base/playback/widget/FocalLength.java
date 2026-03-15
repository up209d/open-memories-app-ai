package com.sony.imaging.app.base.playback.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class FocalLength extends LabelFixedFontFileInfo {
    private static final int FOCAL_LENGTH_THRES = 9999;
    private static final int INDEX_DENOM = 1;
    private static final int INDEX_LENGTH = 2;
    private static final int INDEX_NUMER = 0;
    private static final String SEPARATOR_SLASH = "/";
    private String mFocalLength;

    public FocalLength(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFocalLength = null;
    }

    @Override // com.sony.imaging.app.base.playback.widget.LabelFileInfo
    public void setContentInfo(ContentInfo info) {
        String focalLength = null;
        if (info != null) {
            focalLength = info.getString("FocalLength");
        }
        setValue(focalLength);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.playback.widget.LabelFileInfo, android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        this.mFocalLength = null;
        super.onAttachedToWindow();
    }

    public void setValue(String focalLength) {
        if (focalLength != null) {
            if (!focalLength.equals(this.mFocalLength)) {
                int focalLengthNumer = 0;
                int focalLengthDenom = 0;
                String[] value = focalLength.split(SEPARATOR_SLASH);
                if (value != null && value.length == 2) {
                    focalLengthNumer = Integer.parseInt(value[0]);
                    focalLengthDenom = Integer.parseInt(value[1]);
                }
                setValue(focalLengthNumer, focalLengthDenom);
            }
        } else {
            setVisibility(4);
        }
        this.mFocalLength = focalLength;
    }

    public void setValue(int focalLengthNumer, int focalLengthDenom) {
        int focalLength4Disp = 0;
        boolean isDisplay = false;
        if (focalLengthDenom != 0) {
            focalLength4Disp = focalLengthNumer / focalLengthDenom;
        }
        if (focalLength4Disp >= 1 && focalLength4Disp <= FOCAL_LENGTH_THRES) {
            setText(String.format(getResources().getString(R.string.roamingText1), Integer.valueOf(focalLength4Disp)));
            isDisplay = true;
        }
        setVisibility(isDisplay ? 0 : 4);
    }
}
