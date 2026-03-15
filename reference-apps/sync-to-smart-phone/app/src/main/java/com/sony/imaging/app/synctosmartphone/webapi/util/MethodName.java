package com.sony.imaging.app.synctosmartphone.webapi.util;

/* loaded from: classes.dex */
public class MethodName {
    public static String getMethodName(int offset) {
        return Thread.currentThread().getStackTrace()[offset + 3].getMethodName();
    }

    public static String getMethodName() {
        return getMethodName(1);
    }
}
