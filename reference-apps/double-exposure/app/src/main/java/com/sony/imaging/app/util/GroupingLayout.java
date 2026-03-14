package com.sony.imaging.app.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.sony.imaging.app.R;
import com.sony.imaging.app.util.AbstractRelativeLayoutGroup;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class GroupingLayout extends RelativeLayout implements AbstractRelativeLayoutGroup.IVisibilityChange, IVisibilityChanged {
    protected IVisibilityChanged mNotifyTarget;
    protected int mPriorityId;
    protected ArrayList<View> mSaveViews;

    public GroupingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPriorityId = -1;
        this.mSaveViews = new ArrayList<>();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewGrouping);
        this.mPriorityId = a.getResourceId(0, -1);
        a.recycle();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        int c = getChildCount();
        for (int i = 0; i < c; i++) {
            ((AbstractRelativeLayoutGroup.IVisibilityChange) getChildAt(i)).setCallback(this);
        }
        onVisibilityChanged();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        int c = this.mSaveViews.size();
        for (int i = 0; i < c; i++) {
            addView(this.mSaveViews.get(i));
        }
        this.mSaveViews.clear();
        this.mNotifyTarget = null;
    }

    @Override // com.sony.imaging.app.util.AbstractRelativeLayoutGroup.IVisibilityChange
    public void setCallback(IVisibilityChanged target) {
        this.mNotifyTarget = target;
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        int current = getVisibility();
        super.setVisibility(visibility);
        if (this.mNotifyTarget != null && visibility != current) {
            this.mNotifyTarget.onVisibilityChanged();
        }
    }

    @Override // com.sony.imaging.app.util.IVisibilityChanged
    public void onVisibilityChanged() {
        int visible = 4;
        if (-1 != this.mPriorityId) {
            View priority = findViewById(this.mPriorityId);
            if (priority.getVisibility() == 0) {
                int c = getChildCount();
                for (int i = 0; i < c; i++) {
                    View target = getChildAt(i);
                    if (priority != target) {
                        removeViewAt(i);
                        this.mSaveViews.add(target);
                    }
                }
            } else {
                int c2 = this.mSaveViews.size();
                for (int i2 = 0; i2 < c2; i2++) {
                    addView(this.mSaveViews.get(i2));
                }
                this.mSaveViews.clear();
            }
        }
        int c3 = getChildCount();
        int i3 = 0;
        while (true) {
            if (i3 >= c3) {
                break;
            }
            if (getChildAt(i3).getVisibility() != 0) {
                i3++;
            } else {
                visible = 0;
                break;
            }
        }
        setVisibility(visible);
    }
}
