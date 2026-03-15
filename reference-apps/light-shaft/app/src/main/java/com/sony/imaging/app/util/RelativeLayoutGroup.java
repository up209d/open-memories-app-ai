package com.sony.imaging.app.util;

import android.content.Context;
import android.util.AttributeSet;

/* loaded from: classes.dex */
public class RelativeLayoutGroup extends AbstractRelativeLayoutGroup {
    public RelativeLayoutGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    void setVisibilityOriginal(int visibility) {
        super.setVisibility(visibility);
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        ViewGroupManager.updateStatus(this, this.mGroup);
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
}
