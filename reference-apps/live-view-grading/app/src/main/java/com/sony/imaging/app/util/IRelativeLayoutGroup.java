package com.sony.imaging.app.util;

/* loaded from: classes.dex */
public interface IRelativeLayoutGroup {
    public static final String NORMAL = "main";
    public static final String NOT_EXCLUSIVE = "target";

    String getGroupType();

    boolean isExcluding();

    void onTargetStatusChanged(boolean z);
}
