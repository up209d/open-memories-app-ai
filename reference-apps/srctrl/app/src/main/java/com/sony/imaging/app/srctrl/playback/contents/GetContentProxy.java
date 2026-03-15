package com.sony.imaging.app.srctrl.playback.contents;

import com.sony.imaging.app.srctrl.util.OperationRequester;

/* loaded from: classes.dex */
public class GetContentProxy {
    private static final String TAG = GetContentProxy.class.getSimpleName();

    public static String[] getSchemeList() {
        String[] result = (String[]) new OperationRequester().request(42, new Object[0]);
        if (result == null || result.length <= 0) {
            return null;
        }
        return result;
    }

    public static String[] getSourceList(String scheme) {
        String[] result = (String[]) new OperationRequester().request(43, scheme);
        if (result == null || result.length <= 0) {
            return null;
        }
        return result;
    }
}
