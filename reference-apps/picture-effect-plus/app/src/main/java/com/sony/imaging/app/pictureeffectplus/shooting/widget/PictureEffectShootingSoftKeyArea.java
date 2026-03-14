package com.sony.imaging.app.pictureeffectplus.shooting.widget;

import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class PictureEffectShootingSoftKeyArea {
    public static final int INVALID_IMAGEID = -1;
    protected NotificationListener mCameraListener;
    protected NotificationListener mDisplayListener;
    protected final DisplayModeObserver mDisplayObserver = DisplayModeObserver.getInstance();
}
