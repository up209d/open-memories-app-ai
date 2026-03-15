package com.sony.imaging.app.base.playback;

/* loaded from: classes.dex */
public class LogHelper {
    public static final String MSG_CLOSE_BRACKET = ")";
    public static final String MSG_COLON = " : ";
    public static final String MSG_COMMA = ", ";
    public static final String MSG_CONTENTS = "Contents ";
    public static final String MSG_GROUP = "Group ";
    public static final String MSG_HYPHEN = " - ";
    public static final String MSG_OPEN_BRACKET = "(";
    public static final String MSG_R_ARROW = " -> ";
    private static final ThreadLocal<StringBuilder> mBuilders = new ThreadLocal<>();

    public static StringBuilder getScratchBuilder(String msg) {
        StringBuilder result;
        StringBuilder result2 = mBuilders.get();
        if (msg == null) {
            if (result2 != null) {
                return result2.delete(0, result2.length());
            }
            result = new StringBuilder();
        } else {
            if (result2 != null) {
                return result2.replace(0, result2.length(), msg);
            }
            result = new StringBuilder(msg);
        }
        mBuilders.set(result);
        return result;
    }

    @Deprecated
    public static void onThreadEnd(Thread t) {
    }

    @Deprecated
    public static void reset() {
    }
}
