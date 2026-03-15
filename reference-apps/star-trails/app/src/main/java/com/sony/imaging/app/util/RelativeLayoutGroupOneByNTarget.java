package com.sony.imaging.app.util;

import android.content.Context;
import android.util.AttributeSet;

/* loaded from: classes.dex */
public class RelativeLayoutGroupOneByNTarget extends AbstractRelativeLayoutGroup {
    private static final String TAG = "RelativeLayoutGroupOneByNTarget";

    public RelativeLayoutGroupOneByNTarget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // com.sony.imaging.app.util.AbstractRelativeLayoutGroup
    protected void registerToViewGroupManager() {
        ViewGroupManagerOneByN.register(this, this.mGroup);
    }

    @Override // com.sony.imaging.app.util.AbstractRelativeLayoutGroup
    protected void unregisterFromViewGroupManager() {
        ViewGroupManagerOneByN.unregister(this, this.mGroup);
    }

    void setVisibilityOriginal(int visibility) {
        super.setVisibility(visibility);
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    @Override // com.sony.imaging.app.util.IRelativeLayoutGroup
    public void onTargetStatusChanged(boolean isExcluding) {
        if (isExcluding) {
            setVisibilityOriginal(4);
        } else {
            setVisibilityOriginal(0);
        }
    }

    @Override // com.sony.imaging.app.util.IRelativeLayoutGroup
    public boolean isExcluding() {
        return getVisibility() == 0;
    }

    @Override // com.sony.imaging.app.util.AbstractRelativeLayoutGroup, com.sony.imaging.app.util.IRelativeLayoutGroup
    public String getGroupType() {
        return IRelativeLayoutGroup.NOT_EXCLUSIVE;
    }
}
