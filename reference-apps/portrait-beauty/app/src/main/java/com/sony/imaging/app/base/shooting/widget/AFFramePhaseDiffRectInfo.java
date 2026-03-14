package com.sony.imaging.app.base.shooting.widget;

import android.util.SparseArray;
import android.util.SparseBooleanArray;

/* loaded from: classes.dex */
public class AFFramePhaseDiffRectInfo {
    private SparseArray<AFFramePhaseDiff> mFrames = new SparseArray<>();
    private SparseBooleanArray mIsEnableFrames = new SparseBooleanArray();

    /* JADX INFO: Access modifiers changed from: protected */
    public void putAFFramePhaseDiffFactorAtIndex(int frameIndex, AFFramePhaseDiff f) {
        this.mFrames.put(frameIndex, f);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AFFramePhaseDiff getAFFramePhaseDiffFactorAtIndex(int frameIndex) {
        return this.mFrames.get(frameIndex);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AFFramePhaseDiff getAFFramePhaseDiffFactorAtOrder(int arrayorder) {
        return this.mFrames.valueAt(arrayorder);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void putAFFrameEnableAtIndex(int frameIndex, boolean isEnable) {
        this.mIsEnableFrames.put(frameIndex, isEnable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isAFFrameEnableAtIndex(int frameIndex) {
        return this.mIsEnableFrames.get(frameIndex);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isAFFrameEnableAtOrder(int arrayorder) {
        return this.mIsEnableFrames.valueAt(arrayorder);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getIndexOfEnableAFFrameAtOrder(int arrayorder) {
        return this.mIsEnableFrames.keyAt(arrayorder);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getSize() {
        return this.mFrames.size();
    }
}
