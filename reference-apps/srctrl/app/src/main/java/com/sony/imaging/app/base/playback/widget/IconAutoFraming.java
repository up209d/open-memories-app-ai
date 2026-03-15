package com.sony.imaging.app.base.playback.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.sony.imaging.app.base.playback.contents.ContentInfo;

/* loaded from: classes.dex */
public class IconAutoFraming extends IconFileInfo {
    public IconAutoFraming(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (getDrawable() == null) {
            setImageResource(17306273);
        }
    }

    @Override // com.sony.imaging.app.base.playback.widget.IconFileInfo
    public void setContentInfo(ContentInfo info) {
        int autoframing = -1;
        if (info != null) {
            autoframing = info.getInt("MkNoteAutoFramingInfo");
        }
        setValue(autoframing);
    }

    public void setValue(int autoframing) {
        boolean isDisplay = false;
        if (autoframing != 0 && Integer.MIN_VALUE != autoframing) {
            isDisplay = true;
        }
        setVisibility(isDisplay ? 0 : 4);
    }
}
