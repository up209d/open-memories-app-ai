package com.sony.imaging.app.srctrl.streaming;

import com.sony.imaging.app.srctrl.util.OperationRequester;

/* loaded from: classes.dex */
public class StreamingProxy {
    private static final String TAG = StreamingProxy.class.getSimpleName();

    public static boolean setStreamContent(String uri) {
        Boolean result = (Boolean) new OperationRequester().request(50, uri);
        if (result == null) {
            return false;
        }
        return result.booleanValue();
    }

    public static boolean pauseStreaming() {
        Boolean result = (Boolean) new OperationRequester().request(52, new Object[0]);
        if (result == null) {
            return false;
        }
        return result.booleanValue();
    }

    public static boolean startStreaming() {
        Boolean result = (Boolean) new OperationRequester().request(51, new Object[0]);
        if (result == null) {
            return false;
        }
        return result.booleanValue();
    }

    public static boolean stopStreaming() {
        Boolean result = (Boolean) new OperationRequester().request(53, new Object[0]);
        if (result == null) {
            return true;
        }
        return result.booleanValue();
    }

    public static boolean seekStreamingPosition(int seekmsec) {
        Boolean result = (Boolean) new OperationRequester().request(54, Integer.valueOf(seekmsec));
        if (result == null) {
            return false;
        }
        return result.booleanValue();
    }
}
