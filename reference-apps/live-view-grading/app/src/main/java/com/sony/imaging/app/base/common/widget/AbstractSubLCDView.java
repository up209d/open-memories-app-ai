package com.sony.imaging.app.base.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.sony.imaging.app.base.common.ISubLcdDrawer;
import com.sony.imaging.app.base.common.SubLcdManager;

/* loaded from: classes.dex */
public abstract class AbstractSubLCDView extends View implements ISubLcdDrawer {
    private boolean mIsOwnVisible;
    private int mOutsideVisibility;

    public AbstractSubLCDView(Context context) {
        this(context, null);
    }

    public AbstractSubLCDView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIsOwnVisible = true;
        this.mOutsideVisibility = 0;
        this.mOutsideVisibility = getVisibility();
    }

    public AbstractSubLCDView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIsOwnVisible = true;
        this.mOutsideVisibility = 0;
        this.mOutsideVisibility = getVisibility();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getVisibility() == 0) {
            SubLcdManager.getInstance().requestDraw();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindow() {
        if (getVisibility() == 0) {
            SubLcdManager.getInstance().requestDraw();
        }
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        this.mOutsideVisibility = visibility;
        updateVisibility();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setOwnVisible(boolean isVisible) {
        this.mIsOwnVisible = isVisible;
        updateVisibility();
    }

    protected void setOwnVisibility(int visibility) {
        this.mIsOwnVisible = visibility == 0;
        updateVisibility();
    }

    protected void updateVisibility() {
        int visibility = this.mOutsideVisibility;
        if (!this.mIsOwnVisible && visibility == 0) {
            visibility = 4;
        }
        super.setVisibility(visibility);
    }

    public int getOutsideVisibility() {
        return this.mOutsideVisibility;
    }

    protected boolean isOwnVisible() {
        return this.mIsOwnVisible;
    }

    protected void refresh() {
    }

    protected boolean isVisible() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }
}
