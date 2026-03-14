package com.sony.imaging.app.smoothreflection.shooting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.sony.imaging.app.base.shooting.widget.AntiHandBlurIcon;
import com.sony.imaging.app.smoothreflection.R;

/* loaded from: classes.dex */
public class SmoothReflectionAntiHandBlurIcon extends AntiHandBlurIcon {
    private TypedArray mTypedArray;

    public SmoothReflectionAntiHandBlurIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.AntiHandBlurIcon);
    }

    @Override // com.sony.imaging.app.base.shooting.widget.AntiHandBlurIcon, com.sony.imaging.app.base.shooting.widget.ActiveImage
    protected void refresh() {
        setImageDrawable(this.mTypedArray.getDrawable(0));
    }
}
