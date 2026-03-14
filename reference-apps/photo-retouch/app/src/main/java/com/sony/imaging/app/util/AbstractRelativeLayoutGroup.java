package com.sony.imaging.app.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.sony.imaging.app.R;

/* loaded from: classes.dex */
public abstract class AbstractRelativeLayoutGroup extends RelativeLayout implements IRelativeLayoutGroup, IVisibilityChanged {
    protected String mGroup;
    protected IVisibilityChange mWatchTarget;
    protected int mWatchTargetId;

    /* loaded from: classes.dex */
    public interface IVisibilityChange {
        int getVisibility();

        void setCallback(IVisibilityChanged iVisibilityChanged);
    }

    public AbstractRelativeLayoutGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mGroup = null;
        this.mWatchTargetId = -1;
        this.mWatchTarget = null;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewShowGroup);
        this.mGroup = a.getString(0);
        this.mWatchTargetId = a.getResourceId(1, -1);
        a.recycle();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewGroupManager.register(this, this.mGroup);
        if (-1 != this.mWatchTargetId) {
            this.mWatchTarget = (IVisibilityChange) findViewById(this.mWatchTargetId);
            this.mWatchTarget.setCallback(this);
            onVisibilityChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        ViewGroupManager.unregister(this, this.mGroup);
        super.onDetachedFromWindow();
        this.mWatchTarget = null;
    }

    @Override // com.sony.imaging.app.util.IVisibilityChanged
    public void onVisibilityChanged() {
        if (this.mWatchTarget.getVisibility() == 0) {
            setVisibility(0);
        } else {
            setVisibility(4);
        }
    }
}
