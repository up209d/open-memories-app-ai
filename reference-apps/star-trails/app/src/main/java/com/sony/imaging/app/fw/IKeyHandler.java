package com.sony.imaging.app.fw;

import android.view.KeyEvent;

/* loaded from: classes.dex */
public interface IKeyHandler {
    public static final int HANDLED = 1;
    public static final int INVALID = -1;
    public static final int THROUGH = 0;

    int onKeyDown(int i, KeyEvent keyEvent);

    int onKeyUp(int i, KeyEvent keyEvent);
}
