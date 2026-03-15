package com.sony.imaging.app.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import com.sony.imaging.app.R;

/* loaded from: classes.dex */
public class RelativeLayoutGroupOffset extends AbstractRelativeLayoutGroup {
    private boolean mIsExcluded;
    private int mOffsetH;
    private int mOffsetV;

    public RelativeLayoutGroupOffset(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewOffsetGroup);
        this.mOffsetV = a.getDimensionPixelOffset(0, 0);
        this.mOffsetH = a.getDimensionPixelOffset(1, 0);
        a.recycle();
    }

    @Override // com.sony.imaging.app.util.IRelativeLayoutGroup
    public void onTargetStatusChanged(boolean isExcluding) {
        this.mIsExcluded = isExcluding;
        requestLayout();
    }

    public void forceResetOffset() {
        if (this.mIsExcluded) {
            this.mIsExcluded = false;
            requestLayout();
            ViewGroupManager.updateStatus(this, this.mGroup);
        }
    }

    @Override // android.widget.RelativeLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int count = getChildCount();
        if (this.mIsExcluded) {
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() != 8) {
                    child.offsetTopAndBottom(this.mOffsetV);
                    child.offsetLeftAndRight(this.mOffsetH);
                    child.invalidate();
                }
            }
        }
    }

    @Override // com.sony.imaging.app.util.IRelativeLayoutGroup
    public boolean isExcluding() {
        return !this.mIsExcluded;
    }
}
