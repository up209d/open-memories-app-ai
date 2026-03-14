package com.sony.imaging.app.base.shooting.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/* loaded from: classes.dex */
public class SimulatingEERelativeLayout_Fixation extends SimulatingEERelativeLayout {
    private static final String FIXATION_HEIGHT = "height";
    private static final String FIXATION_WIDTH = "width";
    private String mFixation;

    public SimulatingEERelativeLayout_Fixation(Context context) {
        this(context, null);
    }

    public SimulatingEERelativeLayout_Fixation(Context context, AttributeSet attrs) {
        super(context, attrs);
        String modeStr;
        this.mFixation = FIXATION_HEIGHT;
        if (attrs != null && (modeStr = attrs.getAttributeValue(null, "mode")) != null && modeStr.length() > 0 && modeStr.equals(FIXATION_WIDTH)) {
            this.mFixation = FIXATION_WIDTH;
        }
    }

    @Override // com.sony.imaging.app.base.shooting.widget.SimulatingEERelativeLayout
    public void setLayoutSize(Rect rect) {
        ViewGroup.LayoutParams params = getLayoutParams();
        if (this.mFixation.equals(FIXATION_WIDTH)) {
            params.width = -1;
            params.height = rect.bottom - rect.top;
            rect.left = 0;
        } else {
            params.width = rect.right - rect.left;
            params.height = -1;
            rect.top = 0;
        }
        ViewParent parent = getParent();
        if ((parent instanceof RelativeLayout) || (parent instanceof FrameLayout)) {
            ((ViewGroup.MarginLayoutParams) params).setMargins(rect.left, rect.top, 0, 0);
        } else if (parent instanceof AbsoluteLayout) {
            ((AbsoluteLayout.LayoutParams) params).x = rect.left;
            ((AbsoluteLayout.LayoutParams) params).y = rect.top;
        }
        setLayoutParams(params);
        invalidate();
    }
}
