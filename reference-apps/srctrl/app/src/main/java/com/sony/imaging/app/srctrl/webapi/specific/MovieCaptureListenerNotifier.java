package com.sony.imaging.app.srctrl.webapi.specific;

/* loaded from: classes.dex */
public interface MovieCaptureListenerNotifier {
    public static final int STATUS_ERROR = 1;
    public static final int STATUS_OK = 0;

    void onNotify(int i);
}
