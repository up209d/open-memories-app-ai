package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class IconNDFilterFeedback extends IconFileInfo {
    public static final int ND_FILTER_OFF = 0;
    public static final int ND_FILTER_ON = 1;

    public IconNDFilterFeedback(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ImageView.ScaleType.CENTER);
        Drawable drw = null;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NDFilterFeedbackPb);
        try {
            drw = typedArray.getDrawable(0);
        } catch (Exception e) {
            setVisibility(4);
        }
        setImageDrawable(drw);
    }

    @Override // com.sony.imaging.app.base.playback.widget.IconFileInfo
    public void setContentInfo(ContentInfo info) {
        int ndFilterStatus = -1;
        if (info != null) {
            ndFilterStatus = info.getInt("IsNDFilter");
        }
        setValue(ndFilterStatus);
    }

    public void setValue(int ndFilterStatus) {
        boolean isDisplay = false;
        if (1 == ndFilterStatus) {
            isDisplay = true;
        } else if (ndFilterStatus == 0) {
            isDisplay = false;
        }
        setVisibility(isDisplay ? 0 : 4);
    }
}
