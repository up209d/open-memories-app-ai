package com.sony.imaging.app.util;

import android.util.Log;

/* loaded from: classes.dex */
public class StringBuilderThreadLocal extends ThreadLocal<StringBuilder> {
    public static final String COMMA = ", ";
    public static final String EQUAL = "length = ";
    protected static final String MSG_ERR_ILLEGAL_RELEASE = "releaseScratchBuilder called after getScratchBuilder";
    protected static final String MSG_ERR_ILLEGAL_USE = "getScratchBuilder called without releaseScratchBuilder";
    public static final String MSG_ID = "id = ";
    public static final String MSG_LENGTH = "length = ";
    public static final String MSG_MODE = "mode = ";
    protected static final String MSG_STACK_TRACE = "***** Call stack is ... *****";
    public static final String MSG_STATUS = "status = ";
    public static final String MSG_TAG = "tag = ";
    public static final String MSG_TYPE = "type = ";
    public static final String ROUND_BRACKET_CLOSE = " ) ";
    public static final String ROUND_BRACKET_OPEN = " ( ";
    public static final String SLASH = " / ";
    protected static final String TAG = "StringBuilderThreadLocal";
    protected static StringBuilderThreadLocal sScratchBuilderPool = new StringBuilderThreadLocal();
    protected static ThreadLocal<StringBuilder> sUsedScratchBuilder = new ThreadLocal<>();
    public static boolean sThrowErrOnScratchBuilderDuplicated = false;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // java.lang.ThreadLocal
    public StringBuilder initialValue() {
        return new StringBuilder();
    }

    public static StringBuilder getScratchBuilder() {
        StringBuilder used = sUsedScratchBuilder.get();
        if (used != null) {
            if (sThrowErrOnScratchBuilderDuplicated) {
                throw new IllegalStateException(MSG_ERR_ILLEGAL_USE);
            }
            Log.w(TAG, MSG_ERR_ILLEGAL_USE);
            Log.w(TAG, MSG_STACK_TRACE);
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            for (StackTraceElement stackTraceElement : stack) {
                Log.w(TAG, stackTraceElement.toString());
            }
            sScratchBuilderPool.remove();
            sUsedScratchBuilder.remove();
        }
        StringBuilder builder = sScratchBuilderPool.get();
        sUsedScratchBuilder.set(builder);
        return builder;
    }

    public static void releaseScratchBuilder(StringBuilder builder) {
        if (!sThrowErrOnScratchBuilderDuplicated) {
            StringBuilder cached = sUsedScratchBuilder.get();
            if (builder != cached) {
                Log.w(TAG, MSG_ERR_ILLEGAL_RELEASE);
                Log.w(TAG, MSG_STACK_TRACE);
                StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                for (StackTraceElement stackTraceElement : stack) {
                    Log.w(TAG, stackTraceElement.toString());
                }
                return;
            }
        }
        sUsedScratchBuilder.remove();
    }
}
