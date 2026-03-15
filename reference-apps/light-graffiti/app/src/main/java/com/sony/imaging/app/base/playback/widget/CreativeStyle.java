package com.sony.imaging.app.base.playback.widget;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class CreativeStyle extends IconFileInfo {
    private static final long VAL_PICTUREEFFECT_AUTUMNLEAF = 16;
    private static final long VAL_PICTUREEFFECT_BLACK_WHITE = 6;
    private static final long VAL_PICTUREEFFECT_CLEAR = 13;
    private static final long VAL_PICTUREEFFECT_DEEP = 14;
    private static final long VAL_PICTUREEFFECT_LANDSCAPE = 3;
    private static final long VAL_PICTUREEFFECT_LIGHT = 15;
    private static final long VAL_PICTUREEFFECT_NEUTRAL = 12;
    private static final long VAL_PICTUREEFFECT_NIGHTVIEW = 5;
    private static final long VAL_PICTUREEFFECT_PORTRAIT = 2;
    private static final long VAL_PICTUREEFFECT_SEPIA = 17;
    private static final long VAL_PICTUREEFFECT_STANDARD = 0;
    private static final long VAL_PICTUREEFFECT_SUNSET = 4;
    private static final long VAL_PICTUREEFFECT_VIVID = 1;

    public CreativeStyle(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ImageView.ScaleType.CENTER);
    }

    @Override // com.sony.imaging.app.base.playback.widget.IconFileInfo
    public void setContentInfo(ContentInfo info) {
        long creativeStyle = -1;
        if (info != null) {
            creativeStyle = info.getLong("MkNoteCreativeStyle");
        }
        setValue(creativeStyle);
    }

    public void setValue(long creativeStyle) {
        boolean isDisplay = false;
        if (creativeStyle == 0) {
            setImageResource(17305114);
            isDisplay = true;
        } else if (creativeStyle == VAL_PICTUREEFFECT_VIVID) {
            setImageResource(17305117);
            isDisplay = true;
        } else if (creativeStyle == 2) {
            setImageResource(17305120);
            isDisplay = true;
        } else if (creativeStyle == 3) {
            setImageResource(17305123);
            isDisplay = true;
        } else if (creativeStyle == VAL_PICTUREEFFECT_SUNSET) {
            setImageResource(17305126);
            isDisplay = true;
        } else if (creativeStyle == 6) {
            setImageResource(17305128);
            isDisplay = true;
        } else if (creativeStyle == 5) {
            setImageResource(R.drawable.pointer_hand_icon);
            isDisplay = true;
        } else if (creativeStyle == VAL_PICTUREEFFECT_NEUTRAL) {
            setImageResource(17305132);
            isDisplay = true;
        } else if (creativeStyle == VAL_PICTUREEFFECT_CLEAR) {
            setImageResource(17305136);
            isDisplay = true;
        } else if (creativeStyle == VAL_PICTUREEFFECT_DEEP) {
            setImageResource(17305138);
            isDisplay = true;
        } else if (creativeStyle == VAL_PICTUREEFFECT_LIGHT) {
            setImageResource(R.drawable.pointer_grab_icon);
            isDisplay = true;
        } else if (creativeStyle == VAL_PICTUREEFFECT_AUTUMNLEAF) {
            setImageResource(R.drawable.pointer_grabbing_large);
            isDisplay = true;
        } else if (creativeStyle == VAL_PICTUREEFFECT_SEPIA) {
            setImageResource(R.drawable.pointer_help);
            isDisplay = true;
        }
        setVisibility(isDisplay ? 0 : 4);
    }
}
