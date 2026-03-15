package com.sony.imaging.app.util;

import android.util.SparseArray;

/* loaded from: classes.dex */
public abstract class BeepUtilityIdTableBase {
    public static final String BEEP_ID_DEFAULT = null;
    public static final int KEY_BEEP_PATTERN_INVALID = -1;
    protected SparseArray<SparseArray<String>> mKeyBeepPatternTable = null;

    public abstract void init();

    public SparseArray<String> get(int pattern) {
        if (this.mKeyBeepPatternTable == null) {
            init();
        }
        return this.mKeyBeepPatternTable.get(pattern);
    }
}
