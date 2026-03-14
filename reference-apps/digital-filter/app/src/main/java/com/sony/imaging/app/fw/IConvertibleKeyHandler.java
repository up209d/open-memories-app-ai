package com.sony.imaging.app.fw;

import android.view.KeyEvent;

/* loaded from: classes.dex */
public interface IConvertibleKeyHandler {
    int onConvertedKeyDown(KeyEvent keyEvent, IKeyFunction iKeyFunction);

    int onConvertedKeyUp(KeyEvent keyEvent, IKeyFunction iKeyFunction);
}
