package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.util.Environment;

/* loaded from: classes.dex */
public class SwitchLayoutGroup extends RelativeLayout {
    private int mMaxPfver;
    private int mMinPfver;
    private int mPfver;

    public SwitchLayoutGroup(Context context) {
        super(context);
    }

    public SwitchLayoutGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttribute(context, attrs);
    }

    public SwitchLayoutGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttribute(context, attrs);
    }

    protected void initAttribute(Context context, AttributeSet attrs) {
        this.mPfver = Environment.getVersionPfAPI();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VisiblePFVer);
        this.mMinPfver = a.getInt(0, 1);
        this.mMaxPfver = a.getInt(1, Integer.MAX_VALUE);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mMinPfver > this.mPfver || this.mMaxPfver < this.mPfver) {
            setVisibility(8);
        } else {
            setVisibility(0);
        }
    }
}
