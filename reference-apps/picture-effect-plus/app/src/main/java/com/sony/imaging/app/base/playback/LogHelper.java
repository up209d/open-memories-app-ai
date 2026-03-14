package com.sony.imaging.app.base.playback;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class LogHelper {
    private static final int DEFAULT_MAP_SIZE = 8;
    public static final String MSG_CLOSE_BRACKET = ")";
    public static final String MSG_COLON = " : ";
    public static final String MSG_COMMA = ", ";
    public static final String MSG_CONTENTS = "Contents ";
    public static final String MSG_GROUP = "Group ";
    public static final String MSG_HYPHEN = " - ";
    public static final String MSG_OPEN_BRACKET = "(";
    public static final String MSG_R_ARROW = " -> ";
    private static final Map<Thread, StringBuilder> mBuilders = new HashMap(8);

    public static StringBuilder getScratchBuilder(String msg) {
        StringBuilder result;
        Thread key = Thread.currentThread();
        StringBuilder result2 = mBuilders.get(key);
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
        mBuilders.put(key, result);
        return result;
    }

    public static void onThreadEnd(Thread t) {
        mBuilders.remove(t);
    }

    public static void reset() {
        int activeCount = Thread.activeCount();
        Thread[] actives = new Thread[activeCount];
        List<Thread> l = Arrays.asList(actives);
        Iterator<Map.Entry<Thread, StringBuilder>> it = mBuilders.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Thread, StringBuilder> entry = it.next();
            if (!l.contains(entry.getKey())) {
                it.remove();
            }
        }
    }
}
